SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

source "$SCRIPT_DIR/service-utils.sh"
source "$SCRIPT_DIR/env-config.sh"

echo "Restarting all apps using profile: \"$SPRING_PROFILE\""
echo "Timestamp: $(date '+%Y-%m-%d %H:%M:%S')"
#echo "Secrets: DATASOURCE_PASSWORD=\"$DATASOURCE_PASSWORD\"; SECRET_KEY=\"$SECRET_KEY\"; JKS_KEY=\"$JKS_KEY\""
log_masked_secrets
echo ""

# List of all services to restart
restart_service "eureka.jar" 8761 "/eureka/apps" "Eureka"
restart_service "gateway.jar" 8090 "/gateway/test" "Gateway"
restart_service "config-server.jar" 8888 "/config-server/ping" "Config Server"
restart_service "mas.jar" 8091 "/mas/mas-gateway/test" "MAS"
restart_service "uam.jar" 8092 "/uam/users/test" "UAM"
restart_service "em.jar" 8093 "/em/api/employers/test" "EM"
restart_service "eem.jar" 8094 "/eem/api/employees/test" "EEM"
restart_service "eem2.jar" 8095 "/eem/api/employees/test" "EEM 2"

echo ""
echo "All services restarted successfully at $(date '+%Y-%m-%d %H:%M:%S')"