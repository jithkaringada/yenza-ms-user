#!/usr/bin/env bash
vaultContainerName=yenza-dev-vault
vaultTokenDev=devroot
enableAppRoleAuth=true
if [[ $(docker ps -a -f name=$vaultContainerName -q) ]]; then
  if [[ $(docker ps -f name=$vaultContainerName -q) ]]; then
    echo "${vaultContainerName} is already running"
    enableAppRoleAuth=false
  else
    echo "Starting up existing vault container with name: "
    docker start $vaultContainerName
  fi
else
  echo "Starting up new vault container with id: "
  docker run --cap-add=IPC_LOCK -d -p:8200:8200 --name=${vaultContainerName} -e 'VAULT_DEV_ROOT_TOKEN_ID=devroot' -e 'VAULT_DEV_LISTTEN_ADDRESS=0.0.0.0:8200'  vault "server" "-dev" "-dev-kv-v1"
fi

sleep 5
echo "Adding policy 'dev-policy'"
curl -H "X-Vault-Token:$vaultTokenDev" http://localhost:8200/v1/sys/policies/acl/dev-policy -X PUT -d '{"policy": "# Manage auth methods broadly across Vault\npath \"auth/*\"\n{\n  capabilities = [\"create\", \"read\", \"update\", \"delete\", \"list\", \"sudo\"]\n}\n\n# List, create, update, and delete auth methods\npath \"sys/auth/*\"\n{\n  capabilities = [\"create\", \"read\", \"update\", \"delete\", \"sudo\"]\n}\n\n# List auth methods\npath \"sys/auth\"\n{\n  capabilities = [\"read\"]\n}\n\n# List existing policies\npath \"sys/policies\"\n{\n  capabilities = [\"read\"]\n}\n\n# Create and manage ACL policies broadly across Vault\npath \"sys/policies/*\"\n{\n  capabilities = [\"create\", \"read\", \"update\", \"delete\", \"list\", \"sudo\"]\n}\n\n# List, create, update, and delete key/value secrets\npath \"secret/*\"\n{\n  capabilities = [\"create\", \"read\", \"update\", \"delete\", \"list\", \"sudo\"]\n}\n\n# Manage and manage secret engines broadly across Vault.\npath \"sys/mounts/*\"\n{\n  capabilities = [\"create\", \"read\", \"update\", \"delete\", \"list\", \"sudo\"]\n}\n\n# List existing secret engines.\npath \"sys/mounts\"\n{\n  capabilities = [\"read\"]\n}\n\n# Read health checks\npath \"sys/health\"\n{\n  capabilities = [\"read\", \"sudo\"]\n}"}'

#enable app role
if [[ $enableAppRoleAuth == true ]]; then
  echo "Enabling 'approle' auth type"
  curl -s --header "X-Vault-Token:$vaultTokenDev" --request POST --data '{"type": "approle"}' http://localhost:8200/v1/sys/auth/approle
fi

#create app role
echo "Creating app role 'yenzalo' with dev-policy"
curl -s --header "X-Vault-Token:$vaultTokenDev" --request POST --data '{"policies": "dev-policy"}' http://localhost:8200/v1/auth/approle/role/yenzalo

#get role id
echo "Retrieving role-id for approle 'yenzalo'"
read ROLEID < <(curl -s --header "X-Vault-Token:$vaultTokenDev" http://localhost:8200/v1/auth/approle/role/yenzalo/role-id | jq '."data"."role_id"')

#generate secret id for role
read SECRETID < <(curl -s --header "X-Vault-Token:$vaultTokenDev" --request POST http://localhost:8200/v1/auth/approle/role/yenzalo/secret-id | jq '."data"."secret_id"')

#get token
echo "Retrieving approle token for 'yenzalo'"
read VTOKEN < <(curl -s -H "X-Vault-Token:$vaultTokenDev" -X POST -d "{\"role_id\":$ROLEID, \"secret_id\":$SECRETID}" http://localhost:8200/v1/auth/approle/login | jq '."auth"."client_token"')
echo "Token:$VTOKEN"

#set up oauth client
echo "Posting oauth credentials to vault"
curl -s -H "X-Vault-Token:$vaultTokenDev" -H "Content-Type: application/json" -X POST -d '{"client.id": "test","client.secret": "test"}' http://localhost:8200/v1/secret/private/yenzalo/OAUTH_CLIENT_CREDENTIALS | jq

export VAULT_TOKEN=${VTOKEN}
sleep 10
