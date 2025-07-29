# Employee microservice EEM

This exposes two important features:

## Reconciliation through gRPC between EEM and EM
A scheduler that is configured to be enabled through Config Server
  by changing in GitHub the value of eem.scheduler.enabled (eem-service-<ENV>.yml file)

## A controller method that exposes a simple message to MAS using Kafka /actuator/refresh
MAS is automatically consuming the message through a listener

## Kafka Docker commands (for local ENV rather)
### Read messages
docker run --rm -it \
--network="host" \
confluentinc/cp-kafka:7.5.0 \
kafka-console-consumer \
--bootstrap-server localhost:9092 \
--topic eem.employees.linked \
--from-beginning \
--timeout-ms 3000

### Publish
docker run --rm -it \
--network="host" \
confluentinc/cp-kafka:7.5.0 \
kafka-console-producer \
--broker-list localhost:9092 \
--topic eem.employees.linked


### Purge after retention time
docker run --rm -it \
--network="host" \
confluentinc/cp-kafka:7.5.0 \
kafka-configs \
--bootstrap-server localhost:9092 \
--alter \
--entity-type topics \
--entity-name eem.employees.linked \
--add-config retention.ms=1000,segment.ms=1000

### Reset purge after retention
docker run --rm -it \
--network="host" \
confluentinc/cp-kafka:7.5.0 \
kafka-configs \
--bootstrap-server localhost:9092 \
--alter \
--entity-type topics \
--entity-name eem.employees.linked \
--delete-config retention.ms