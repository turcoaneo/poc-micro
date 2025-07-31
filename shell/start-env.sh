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
  if [ "$name" = "EEM" ]; then
    java $jvm_flags -Dspring.profiles.active="$SPRING_PROFILE" \
         -Dio.grpc.internal.ManagedChannelImpl=DEBUG \
         "-Deem.scheduler.cron=$EEM_SCHEDULER_CRON" \
         $extra_flags \
         -jar "/apps/$jar" --server.port="$port" $grpc_port &
  elif [ "$name" = "EEM2" ]; then
    java $jvm_flags -Dspring.profiles.active="$SPRING_PROFILE" \
         -Dspring.mvc.servlet.path=/eem-2 \
         "-Deem.scheduler.cron=$EEM_SCHEDULER_CRON_2" \
         $extra_flags \
         -jar "/apps/$jar" --server.port="$port" $grpc_port &
  else
    java $jvm_flags -Dspring.profiles.active="$SPRING_PROFILE" \
         $extra_flags \
         -jar "/apps/$jar" --server.port="$port" $grpc_port &
  fi
  sleep 10

  wait_for_service "$port" "$health_path" "$name"

  local end_time
  end_time=$(date +%s)
  local duration=$(( end_time - start_time ))
  echo "$(date '+%Y-%m-%d %H:%M:%S') - $name ready after ${duration}s"
}

# Service Launch Sequence
launch_app "Eureka" "-Xms128m -Xmx256m" "eureka.jar" 8761 "" "/test/ping"

detect_host_eip

launch_app "Gateway" "-Xms128m -Xmx256m" "gateway.jar" 8090 "" "/gateway/test" \
  -Dmanagement.tracing.enabled=false

launch_app "Config Server" "-Xms128m -Xmx256m" "config-server.jar" 8888 "" "/config-server/ping"

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

  if [ -z "$EEM_SCHEDULER_CRON" ]; then
    echo "EEM_SCHEDULER_CRON is not set!"
  else
    echo "EEM_SCHEDULER_CRON=$EEM_SCHEDULER_CRON"
  fi

launch_app "EEM" "-Xms512m -Xmx1024m" "eem.jar" 8094 "--grpc.server.port=9094" "/eem/api/employees/test" \
  -Dspring.datasource.password="$DATASOURCE_PASSWORD" \
  -Dspring.datasource.url="$SPRING_DATASOURCE_URL_EEM" \
  -Dspring.jpa.hibernate.ddl-auto=update \
  -Dmanagement.tracing.enabled=false \
  -Dkafka.enabled=false \
  -Deem.scheduler.enabled=true

  if [ -z "$EEM_SCHEDULER_CRON_2" ]; then
    echo "EEM_SCHEDULER_CRON_2 is not set!"
  else
    echo "EEM_SCHEDULER_CRON_2=$EEM_SCHEDULER_CRON_2"
  fi

launch_app "EEM2" "-Xms512m -Xmx1024m" "eem2.jar" 8095 "--grpc.server.port=9095" "/eem-2/api/employees/test" \
  -Dspring.datasource.password="$DATASOURCE_PASSWORD" \
  -Dspring.datasource.url="$SPRING_DATASOURCE_URL_EEM" \
  -Dspring.jpa.hibernate.ddl-auto=update \
  -Dmanagement.tracing.enabled=false \
  -Dkafka.enabled=true \
  -Deem.scheduler.enabled=false \
  -Djavax.net.debug=ssl:handshake

launch_app "MAS" "-Xms512m -Xmx1024m" "mas.jar" 8091 "" "/mas/mas-gateway/test" \
  -Dmanagement.tracing.enabled=false \
  -Dspring.datasource.password="$DATASOURCE_PASSWORD" \
  -Djavax.net.debug=ssl:handshake

wait

echo "$(date '+%Y-%m-%d %H:%M:%S') - All services launched successfully."