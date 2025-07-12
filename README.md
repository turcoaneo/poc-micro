# Proof of concept for microservices

This is parent, no executable jar.

## Instructions
docker run -p 3307:3306 --name pumi-dev-mysql -e MYSQL_ROOT_PASSWORD=*** -e MYSQL_DATABASE=dev_db -d mysql:8.4.4

### create schemas in MySQL (Docker) ENV
CREATE DATABASE employer_db;
CREATE DATABASE employee_db;
CREATE DATABASE main_service_db;
CREATE DATABASE user_auth_db; 

### create test schemas in MySQL (Docker) ENV
#### user_auth_db uses in-mem h2-db for repo testing
CREATE DATABASE employer_db_test;
CREATE DATABASE employee_db_test;


### Docker
./build-monolith.sh
aws ecr get-login-password --region eu-north-1 | docker login --username AWS --password-stdin 509399624827.dkr.ecr.eu-north-1.amazonaws.com
docker tag monolith-poc:latest 509399624827.dkr.ecr.eu-north-1.amazonaws.com/poc-micro-uat:latest
docker push 509399624827.dkr.ecr.eu-north-1.amazonaws.com/poc-micro-uat:latest