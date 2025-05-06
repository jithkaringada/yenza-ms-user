#!/usr/bin/env bash
./run_vault.sh
java -Duser.timezone=UTC -Ddebug -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5009 -Xms256m -XX:+UseG1GC -XX:+UseStringDeduplication -DAPP_VERSION=2 -DSERVICE_ENDPOINT="http://$Address:8080" -jar build/libs/yenza-ms-user-1.0.jar