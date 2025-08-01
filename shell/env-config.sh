#!/bin/bash

# Detect profile
SPRING_PROFILE="${SPRING_PROFILE:-local}"
export SPRING_PROFILE

export_docker_datasource_urls() {
  export SPRING_DATASOURCE_URL_EM="jdbc:mysql://host.docker.internal:3307/employer_db?allowPublicKeyRetrieval=true&useSSL=false"
  export SPRING_DATASOURCE_URL_EEM="jdbc:mysql://host.docker.internal:3307/employee_db?allowPublicKeyRetrieval=true&useSSL=false"
  export SPRING_DATASOURCE_URL_UAM="jdbc:mysql://host.docker.internal:3307/user_auth_db?allowPublicKeyRetrieval=true&useSSL=false"
}

export_local_datasource_urls() {
  export SPRING_DATASOURCE_URL_EM="jdbc:mysql://127.0.0.1:3307/employer_db"
  export SPRING_DATASOURCE_URL_EEM="jdbc:mysql://127.0.0.1:3307/employee_db"
  export SPRING_DATASOURCE_URL_UAM="jdbc:mysql://127.0.0.1:3307/user_auth_db"
}

export_uat_datasource_urls() {
  export SPRING_DATASOURCE_URL_EM="jdbc:mysql://mysql-community-db.cn48a44e27uj.eu-north-1.rds.amazonaws.com:3306/employer_db"
  export SPRING_DATASOURCE_URL_EEM="jdbc:mysql://mysql-community-db.cn48a44e27uj.eu-north-1.rds.amazonaws.com:3306/employee_db"
  export SPRING_DATASOURCE_URL_UAM="jdbc:mysql://mysql-community-db.cn48a44e27uj.eu-north-1.rds.amazonaws.com:3306/user_auth_db"
}

export HOST="localhost"
if [[ "$SPRING_PROFILE" == "uat" ]]; then
  export_uat_datasource_urls
#  export HOST="poc-alb-1312740255.eu-north-1.elb.amazonaws.com"
else
  export_docker_datasource_urls
fi



echo "EM: $SPRING_DATASOURCE_URL_EM"
echo "EEM: $SPRING_DATASOURCE_URL_EEM"
echo "UAM: $SPRING_DATASOURCE_URL_UAM"



# Secrets (could also be injected via env at runtime)
export DATASOURCE_PASSWORD="${DATASOURCE_PASSWORD:-xxx}"
export SECRET_KEY="${SECRET_KEY:-xxx}"
export JKS_KEY="${JKS_KEY:-xxx}"