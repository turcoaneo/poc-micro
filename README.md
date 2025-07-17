# Proof of concept for microservices

This is parent, no executable jar.

- MAS (main app service) 8091
- UAM (user auth service) 8092
- EM (employer service) 8093 - gRPC port 9093
- EEM (employee auth service) 8094 - gRPC port 9095
- EEM 2 (load balanced EEM) 8095 - gRPC port 9095
- Eureka 8761
- GTW (Spring Cloud API Gateway) 8090 (management port: 8443)
- CFG (Spring Cloud Config Server) 8888

Others:
- MySQL 3037
- Kafka 9092
- Zipkin 9411

## Instructions

### Docker containers
docker run -p 3307:3306 --name pumi-dev-mysql -e MYSQL_ROOT_PASSWORD=*** -e MYSQL_DATABASE=dev_db -d mysql:8.4.4
docker run -d -p 9411:9411 openzipkin/zipkin
#### Run Kafka from docker-compose.yml


### create schemas in MySQL (Docker) ENV
CREATE DATABASE employer_db;
CREATE DATABASE employee_db;
CREATE DATABASE main_service_db;
CREATE DATABASE user_auth_db; 

### create test schemas in MySQL (Docker) ENV
#### user_auth_db uses in-mem h2-db for repo testing
CREATE DATABASE employer_db_test;
CREATE DATABASE employee_db_test;


### Docker plus AWS
./build-monolith.sh
aws ecr get-login-password --region eu-north-1 | docker login --username AWS --password-stdin <AWS ID>.dkr.ecr.eu-north-1.amazonaws.com
docker tag monolith-poc:latest <AWS ID>.dkr.ecr.eu-north-1.amazonaws.com/poc-micro-uat:latest
docker push <AWS ID>.dkr.ecr.eu-north-1.amazonaws.com/poc-micro-uat:latest

## Run

### Local - with IntelliJ
Go to Services (ALT+8)
Spring Boot: Run all services

Access Swagger pages: http://localhost:<port>/<service-name>/swagger-ui/index.html

#### Port - service-name map / dict: 8091 -> mas; 8092 -> uam; 8093 -> em; 8094 -> eem; 8095 -> eem

Example:
http://localhost:8091/mas/swagger-ui/index.html

### AWS
Access Swagger pages: http://poc-alb-1312740255.eu-north-1.elb.amazonaws.com/<service-name>/swagger-ui/index.html

Example
http://poc-alb-1312740255.eu-north-1.elb.amazonaws.com/uam/swagger-ui/index.html

## How to

### Register user type with '/register' (no JWT required), e.g., test - 123
select * from user_auth_db.user_account;

### Login with '/login' (no JWT required), e.g., test - 123

### Copy JWT and insert in Swagger right top component - Authorize (60' life duration) - \
###     use controllers, see each service README