#!/bin/bash

detect_host() {
  SPRING_PROFILE="${SPRING_PROFILE:-uat}"
  if [[ "$SPRING_PROFILE" == "local" ]]; then
    export HOST="localhost"
  else
    echo "Waiting for IP to appear at /tmp/eureka-ip.txt..."

        local timeout="${MAX_WAIT:-120}"
        while [[ ! -f /tmp/eureka-ip.txt && $timeout -gt 0 ]]; do
          sleep 1
          ((timeout--))
        done

        if [[ $timeout -eq 0 ]]; then
          echo "Timed out waiting for Eureka IP file."
#          exit 1
        fi

        if ! grep -Eq '^[0-9]+\.[0-9]+\.[0-9]+\.[0-9]+$' /tmp/eureka-ip.txt; then
          echo "DIP file is present but malformed."
        fi

        if [[ "$SPRING_PROFILE" == "uat" ]]; then

          grep -q "169.254.169.253" /etc/resolv.conf || echo "nameserver 169.254.169.253" >> /etc/resolv.conf

          HOST=$(cat /tmp/eureka-ip.txt)
          export HOST
          echo "Discovered $name IP: $HOST"

          grep -q "$HOST" /etc/hosts || echo "$HOST mysql-community-db.cn48a44e27uj.eu-north-1.rds.amazonaws.com" >> /etc/hosts

          echo "=== DNS Resolver Check ==="
          cat /etc/resolv.conf
        fi

        log_masked_secrets
  fi
}

# Generic service wait function
wait_for_service() {
  local port=$1
  local path=$2
  local name=$3
  local max_wait="${MAX_WAIT:-300}"
  local elapsed=0

  echo "Checking $name availability at $HOST:$port$path..."
  if curl --silent --fail "http://$HOST:$port$path" > /dev/null; then
    echo -e "\n$name already up — skipping wait."
  else
    echo "Waiting for $name to become available..."
    while [[ $elapsed -lt $max_wait ]]; do
      printf '.'
      sleep 2
      elapsed=$((elapsed + 2))
      if curl --silent --fail "http://$HOST:$port$path" > /dev/null; then
        echo -e "\n$name Server is now up!"
        break
      fi
    done
    [[ $elapsed -ge $max_wait ]] && echo -e "\n$name not reachable after ${max_wait}s."
  fi
}

# Generic service restart by jar name
restart_service() {
  local jar_name=$1
  local port=$2
  local path=$3
  local service_name=$4
  local profile="${SPRING_PROFILE:-local}"
  local pass="${DATASOURCE_PASSWORD:-xxx}"
  local jdbc_url_var="SPRING_DATASOURCE_URL_${service_name^^}" # UAM → SPRING_DATASOURCE_URL_UAM
  local jdbc_url="${!jdbc_url_var}"

  # Kill existing process
  PID=$(pgrep -f "java.*${jar_name}")
  if [[ -n "$PID" ]]; then
    echo "Stopping $service_name (PID: $PID)..."
    kill -15 "$PID"
    sleep 2
  else
    echo "$service_name not running. Starting new instance..."
  fi

  echo "$(date '+%Y-%m-%d %H:%M:%S') - Restarting $service_name..."

  mvc_servlet_path=$(echo "/${path#/}" | cut -d'/' -f1,2)
  echo "Mvc path is $mvc_servlet_path"

  java -Dspring.profiles.active="$profile" \
     -Dspring.datasource.password="$pass" \
     -Dspring.datasource.url="$jdbc_url" \
     -Dmanagement.tracing.enabled=false \
     -Dspring.mvc.servlet.path="$mvc_servlet_path" \
     -jar "/apps/${jar_name}" --server.port="$port" &

  wait_for_service "$port" "$path" "$service_name"
}

# Mask a secret by keeping 3 middle chars, masking the rest
mask_secret() {
  local secret="$1"
  local mask_char="•"
  local len=${#secret}

  if (( len <= 3 )); then
    echo "${mask_char}${secret:0:1}${mask_char}"
    return
  fi

  local mid=$((len / 2))
  local start=$((mid - 1))
  local masked
  masked="${mask_char:0:1}$(printf "%.0s$mask_char" $(seq 1 $start))${secret:$start:3}$(printf "%.0s$mask_char" $(seq 1 $((len - start - 3))) )"
  echo "$masked"
}

log_masked_secrets() {
  echo "Secrets:"
  echo "  DATASOURCE_PASSWORD=\"$(mask_secret "$DATASOURCE_PASSWORD")\""
  echo "  SECRET_KEY=\"$(mask_secret "$SECRET_KEY")\""
  echo "  JKS_KEY=\"$(mask_secret "$JKS_KEY")\""
}

