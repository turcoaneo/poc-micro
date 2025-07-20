#!/bin/bash

SERVICE_NAME=$1
JAR_NAME=$2
PORT=$3
HEALTH_PATH=$4

echo "MOCK RESTART SCRIPT EXECUTED"
echo "Service Name: $SERVICE_NAME"
echo "Jar Name: $JAR_NAME"
echo "Port: $PORT"
echo "Health Path: $HEALTH_PATH"

echo "$(date '+%Y-%m-%d %H:%M:%S') - Simulating restart for $SERVICE_NAME..."
sleep 1
echo "$SERVICE_NAME restarted on port $PORT (mocked)"