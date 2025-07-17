#!/bin/bash
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$SCRIPT_DIR/service-utils.sh"
source "$SCRIPT_DIR/env-config.sh"

launch_app() {
  local name="$1"
  local jvm_flags="$2"
  local jar="$3"
  local port="$4"
  local grpc_port="$5"
  local health_path="$6"
  shift 6
  local extra_flags="$*"

  local start_time
  start_time=$(date +%s)
  echo "$(date '+%Y-%m-%d %H:%M:%S') - Launching $name..."

  # shellcheck disable=SC2086
  java $jvm_flags -Dspring.profiles.active="$SPRING_PROFILE" $extra_flags \
       -jar "/apps/$jar" --server.port="$port" $grpc_port &
  sleep 10

  wait_for_service "$port" "$health_path" "$name"

  local end_time
  end_time=$(date +%s)
  local duration=$(( end_time - start_time ))
  echo "$(date '+%Y-%m-%d %H:%M:%S') - $name ready after ${duration}s"
}

# Service Launch Sequence
launch_app "Eureka" "-Xms128m -Xmx256m" "eureka.jar" 8761 "" "/test/ping"

detect_host

launch_app "Gateway" "-Xms128m -Xmx256m" "gateway.jar" 8090 "" "/gateway/test" \
  -Dmanagement.tracing.enabled=false

launch_app "Config Server" "-Xms128m -Xmx256m" "config-server.jar" 8888 "" "/config-server/ping"

launch_app "MAS" "-Xms256m -Xmx512m" "mas.jar" 8091 "" "/mas/mas-gateway/test" \
  -Dmanagement.tracing.enabled=false \
  -Dspring.datasource.password="$DATASOURCE_PASSWORD"

launch_app "UAM" "-Xms512m -Xmx1024m" "uam.jar" 8092 "" "/uam/users/test" \
  -Dspring.datasource.password="$DATASOURCE_PASSWORD" \
  -Dspring.datasource.url="$SPRING_DATASOURCE_URL_UAM" \
  -Dspring.jpa.hibernate.ddl-auto=update \
  -Dmanagement.tracing.enabled=false

launch_app "EM" "-Xms512m -Xmx1024m" "em.jar" 8093 "--grpc.server.port=9093" "/em/api/employers/test" \
  -Dspring.datasource.password="$DATASOURCE_PASSWORD" \
  -Dspring.datasource.url="$SPRING_DATASOURCE_URL_EM" \
  -Dspring.jpa.hibernate.ddl-auto=update \
  -Dmanagement.tracing.enabled=false

launch_app "EEM" "-Xms512m -Xmx1024m" "eem.jar" 8094 "--grpc.server.port=9094" "/eem/api/employees/test" \
  -Dspring.datasource.password="$DATASOURCE_PASSWORD" \
  -Dspring.datasource.url="$SPRING_DATASOURCE_URL_EEM" \
  -Dspring.jpa.hibernate.ddl-auto=update \
  -Dkafka.hostname=host.docker.internal \
  -Dmanagement.tracing.enabled=false

launch_app "EEM2" "-Xms512m -Xmx1024m" "eem2.jar" 8095 "--grpc.server.port=9095" "/eem-2/api/employees/test" \
  -Dspring.datasource.password="$DATASOURCE_PASSWORD" \
  -Dspring.datasource.url="$SPRING_DATASOURCE_URL_EEM" \
  -Dkafka.hostname=host.docker.internal \
  -Dmanagement.tracing.enabled=false \
  -Dspring.mvc.servlet.path=/eem-2

wait

echo "$(date '+%Y-%m-%d %H:%M:%S') - All services launched successfully."