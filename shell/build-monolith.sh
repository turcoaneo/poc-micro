#!/bin/bash

set -e

echo "Running Maven build for all modules..."
#mvn clean install -Dmaven.test.skip=true

echo "Creating jars/ directory..."
rm -rf jars
mkdir jars

echo "Collecting service JARs..."
cp main-app/target/*.jar jars/mas.jar
cp user-auth/target/*.jar jars/uam.jar
cp employer-app/target/*.jar jars/em.jar
cp employee-app/target/*.jar jars/eem.jar
cp employee-app/target/*.jar jars/eem2.jar
cp eureka/target/*.jar jars/eureka.jar
cp gateway/target/*.jar jars/gateway.jar
cp config-server/target/*.jar jars/config-server.jar

echo "Building Docker image..."
docker build -t monolith-poc .
#docker build --no-cache -t monolith-poc .

echo "Done! Docker image 'monolith-poc' is ready to launch."