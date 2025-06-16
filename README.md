# Proof of concept for microservices

This is parent, no executable jar.

## Instructions

### create schemas in MySQL (Docker) ENV
CREATE DATABASE employer_db;
CREATE DATABASE employee_db;
CREATE DATABASE main_service_db;
CREATE DATABASE user_auth_db; 

### create test schemas in MySQL (Docker) ENV
#### user_auth_db uses in-mem h2-db for repo testing
CREATE DATABASE employer_db_test;
CREATE DATABASE employee_db_test;