#!/bin/bash
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

source "$SCRIPT_DIR/service-utils.sh"
source "$SCRIPT_DIR/env-config.sh"

# Service Façade Command
SERVICE_NAME="$1"          # UAM
JAR_NAME="$2"              # uam.jar
PORT="$3"                  # 8092
HEALTH_PATH="$4"           # /uam/users/test

if [[ -z "$SERVICE_NAME" || -z "$JAR_NAME" || -z "$PORT" || -z "$HEALTH_PATH" ]]; then
  echo "Usage: ./restart-app.sh <serviceName> <jarName> <port> <healthPath>"
  exit 1
fi

echo "$(date '+%Y-%m-%d %H:%M:%S') - Restarting service: $SERVICE_NAME"
restart_service "$JAR_NAME" "$PORT" "$HEALTH_PATH" "$SERVICE_NAME"