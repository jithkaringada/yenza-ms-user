#!/usr/bin/env bash
./run_vault.sh
java --add-modules java.se --add-exports java.base/jdk.internal.ref=ALL-UNNAMED --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.nio=ALL-UNNAMED --add-opens java.base/sun.nio.ch=ALL-UNNAMED --add-opens java.management/sun.management=ALL-UNNAMED --add-opens jdk.management/com.sun.management.internal=ALL-UNNAMED -Duser.timezone=UTC -Xms256m -XX:+UseG1GC -XX:+UseStringDeduplication -DAPP_VERSION=2 -jar build/libs/yenza-ms-gateway-1.0.jar
