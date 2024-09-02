#!/bin/bash
####!/usr/bin/env sh
function kubectl () {
    minikube.exe kubectl "--" "$@"
}
function minikube () {
    minikube.exe "$@"
}
#alias kubectl="minikube.exe kubectl --"
#alias minikube="minikube.exe"
#current=$(pwd -W tr / \\)
current=$(pwd)
echo $current
apisPath=$(grep -ioP "(apisPath\s*=\s*)\K.+" _custom_deploy.properties)
uiPath=$(grep -ioP "(uiPath\s*=\s*)\K.+" _custom_deploy.properties)

[[ "$apisPath" = "default"  ]] && apisPath="${current}/../services"
[[ "$uiPath" = "default" ]] && uiPath="${current}/../front"

# Minikube namespace
project=$(grep -ioP "(project\s*=\s*)\K.+" _custom_deploy.properties)

# Databases
postgreSQL=$(grep -ioP "(postgreSQL\s*=\s*)\K.+" _custom_deploy.properties)
influxDB=$(grep -ioP "(influxDB\s*=\s*)\K.+" _custom_deploy.properties)
# APIs
hdr=$(grep -ioP "(hdr\s*=\s*)\K.+" _custom_deploy.properties)
kpi=$(grep -ioP "(kpi\s*=\s*)\K.+" _custom_deploy.properties)
influx=$(grep -ioP "(influx\s*=\s*)\K.+" _custom_deploy.properties)
ingestion=$(grep -ioP "(ingestion\s*=\s*)\K.+" _custom_deploy.properties)
user=$(grep -ioP "(user\s*=\s*)\K.+" _custom_deploy.properties)
baseApi=$(grep -ioP "(base\s*=\s*)\K.+" _custom_deploy.properties)
wrapperApi=$(grep -ioP "(wrapper\s*=\s*)\K.+" _custom_deploy.properties)
dataApi=$(grep -ioP "(data\s*=\s*)\K.+" _custom_deploy.properties)
kubeflowApi=$(grep -ioP "(kubeflow\s*=\s*)\K.+" _custom_deploy.properties)
keycloakInitializer=$(grep -ioP "(keycloakInitializer\s*=\s*)\K.+" _custom_deploy.properties)
# Others
ui=$(grep -ioP "(ui\s*=\s*)\K.+" _custom_deploy.properties)
keycloak=$(grep -ioP "(keycloak\s*=\s*)\K.+" _custom_deploy.properties)
grafana=$(grep -ioP "(grafana\s*=\s*)\K.+" _custom_deploy.properties)
nifi=$(grep -ioP "(nifi\s*=\s*)\K.+" _custom_deploy.properties)
wso2=$(grep -ioP "(wso2\s*=\s*)\K.+" _custom_deploy.properties)


echo "Start"
# Connect to Minikube
if ! minikube status &> /dev/null;
then 
    minikube start --driver=hyperv 
fi
if minikube status;
then
    kubectl create namespace $project

# DEPLOY DATABASES

    if [[ $postgreSQL = 'true' ]]
    then
        cd  "${current}/docker_config_local/Databases/postgresql"
        # POSTGRESQL INSTALLATION
        # set environment variables
        eval $(minikube docker-env)

        kubectl delete configmaps/postgresql-db-config --namespace=$project
        kubectl delete statefulsets/postgresql-db --namespace=$project
        kubectl delete services/postgresql-db-sv --namespace=$project
        
        docker build --no-cache --force-rm --tag=postgresql-db:latest .

        kubectl apply -f postgresql-configmap.yaml --namespace=$project
        kubectl apply -f postgresql-statefulset.yaml --namespace=$project
        kubectl apply -f postgresql-service.yaml --namespace=$project
        
        while [[ $(kubectl -n ${project} get pods -l app=postgresql-db -o 'jsonpath={..status.conditions[?(@.type=="Ready")].status}') != "True" ]]; 
            do echo "Waiting to PostgreSQL pod to create databases" && sleep 1; 
        done
        kubectl exec statefulset/postgresql-db --namespace=$project -- bin/bash -c "psql -U postgres < ./scripts/init.sql"
    fi

    if [[ $influxDB = 'true' ]]
    then
        cd  "${current}/docker_config_local/Databases/influxdb"
        # INFLUXDB INSTALLATION
        # set environment variables
        eval $(minikube docker-env)

        kubectl delete deployments/influx-db --namespace=$project
        kubectl delete services/influx-db-sv --namespace=$project
        
        docker build --no-cache --force-rm --tag=influx:latest .
        
        kubectl apply -f influxdb-volume.yaml --namespace=$project
        kubectl apply -f influxdb-secrets.yaml --namespace=$project
        kubectl apply -f influxdb-deployment.yaml --namespace=$project
        kubectl apply -f influxdb-service.yaml --namespace=$project
    fi
# DEPLOY APIs

    if [[ $baseApi = 'true' ]]
    then
        cd "${apisPath}/REN_API/baseApi"
        mvn clean package -Dmaven.test.skip
        cp "./target/"*.jar "${current}/docker_config_local/APIs/base-api/api.jar"

        cd "${current}/docker_config_local/APIs/base-api"
        # API INSTALLATION
        # set environment variables
        eval $(minikube docker-env)

        # delete kubernetes resources if exists
        kubectl delete deployments/base-api --namespace=$project
        kubectl delete services/base-api-sv --namespace=$project

        docker build --no-cache --force-rm --tag=base-api:latest .

        # create kubernetes resources
        kubectl apply -f base-api-deployment.yaml --force=true --namespace=$project
        kubectl apply -f base-api-service.yaml --namespace=$project
    fi

    if [[ $hdr = 'true' ]]
    then
        cd "${apisPath}/hdrAPI"
        mvn clean package -Dmaven.test.skip
        cp "./target/"*.jar "${current}/docker_config_local/APIs/hdr-api/api.jar"

        cd "${current}/docker_config_local/APIs/hdr-api"
        # API INSTALLATION
        # set environment variables
        eval $(minikube docker-env)

        # delete kubernetes resources if exists
        kubectl delete deployments/hdr-api --namespace=$project
        kubectl delete services/hdr-api-sv --namespace=$project

        docker build --no-cache --force-rm --tag=hdr-api:latest .

        # create kubernetes resources
        kubectl apply -f hdr-api-deployment.yaml --force=true --namespace=$project
        kubectl apply -f hdr-api-service.yaml --namespace=$project
    fi

    if [[ $influx = 'true' ]]
    then
        cd "${apisPath}/measurementapi"
        mvn clean package -Dmaven.test.skip
        cp "./target/"*.jar "${current}/docker_config_local/APIs/influx-api/api.jar"

        cd "${current}/docker_config_local/APIs/influx-api"
        # API INSTALLATION
        # set environment variables
        eval $(minikube docker-env)

        # delete kubernetes resources if exists
        kubectl delete deployments/influx-api --namespace=$project
        kubectl delete services/influx-api-sv --namespace=$project

        docker build --no-cache --force-rm --tag=influx-api:latest .

        # create kubernetes resources
        kubectl apply -f influx-api-deployment.yaml --force=true --namespace=$project
        kubectl apply -f influx-api-service.yaml --namespace=$project
    fi

    if [[ $kpi = 'true' ]]
    then
        cd "${apisPath}/kpiAPI"
        mvn clean package -Dmaven.test.skip
        cp "./target/"*.jar "${current}/docker_config_local/APIs/kpi-api/api.jar"

        cd "${current}/docker_config_local/APIs/kpi-api"
        # API INSTALLATION
        # set environment variables
        eval $(minikube docker-env)

        # delete kubernetes resources if exists
        kubectl delete deployments/kpi-api --namespace=$project
        kubectl delete services/kpi-api-sv --namespace=$project

        docker build --no-cache --force-rm --tag=kpi-api:latest .

        # create kubernetes resources
        kubectl apply -f kpi-api-deployment.yaml --namespace=$project
        kubectl apply -f kpi-api-service.yaml --namespace=$project
    fi

    if [[ $ingestion = 'true' ]]
    then
        cd "${apisPath}/ingestionAPI"
        mvn clean package -Dmaven.test.skip
        cp "./target/"*.jar "${current}/docker_config_local/APIs/ingestion-api/api.jar"

        cd "${current}/docker_config_local/APIs/ingestion-api"
        # API INSTALLATION
        # set environment variables
        eval $(minikube docker-env)

        # delete kubernetes resources if exists
        kubectl delete deployments/ingestion-api --namespace=$project
        kubectl delete services/ingestion-api-sv --namespace=$project

        docker build --no-cache --force-rm --tag=ingestion-api:latest .

        # create kubernetes resources
        kubectl apply -f ingestion-api-deployment.yaml --force=true --namespace=$project
        kubectl apply -f ingestion-api-service.yaml --namespace=$project
    fi

    if [[ $user = 'true' ]]
    then
        cd "${apisPath}/REN_API/userAPI"
        mvn clean package -Dmaven.test.skip
        cp "./target/"*.jar "${current}/docker_config_local/APIs/user-api/api.jar"

        cd "${current}/docker_config_local/APIs/user-api"
        # API INSTALLATION
        # set environment variables
        eval $(minikube docker-env)

        # delete kubernetes resources if exists
        kubectl delete deployments/user-api --namespace=$project
        kubectl delete services/user-api-sv --namespace=$project

        docker build --no-cache --force-rm --tag=user-api:latest .

        # create kubernetes resources
        kubectl apply -f user-api-deployment.yaml --force=true --namespace=$project
        kubectl apply -f user-api-service.yaml --namespace=$project
    fi

    if [[ $keycloakInitializer = 'true' ]]
    then
        cd "${apisPath}/keycloakInit"
        mvn clean package -Dmaven.test.skip
        cp "./target/"*.jar "${current}/docker_config_local/APIs/keycloak-init/api.jar"

        cd "${current}/docker_config_local/APIs/keycloak-init"
        # API INSTALLATION
        # set environment variables
        eval $(minikube docker-env)

        # delete kubernetes resources if exists
        kubectl delete deployments/keycloak-init --namespace=$project
        kubectl delete services/keycloak-init-sv --namespace=$project

        docker build --no-cache --force-rm --tag=keycloak-init:latest .

        # create kubernetes resources
        kubectl apply -f keycloak-init-deployment.yaml --force=true --namespace=$project
        kubectl apply -f keycloak-init-service.yaml --namespace=$project
    fi

    if [[ $wrapperApi = 'true' ]]
    then
        cd "${apisPath}/REN_API/wrapperAPI"
        mvn clean package -Dmaven.test.skip
        cp "./target/"*.jar "${current}/docker_config_local/APIs/wrapper-api/api.jar"

        cd "${current}/docker_config_local/APIs/wrapper-api"
        # API INSTALLATION
        # set environment variables
        eval $(minikube docker-env)

        # delete kubernetes resources if exists
        kubectl delete deployments/wrapper-api --namespace=$project
        kubectl delete services/wrapper-api-sv --namespace=$project

        docker build --no-cache --force-rm --tag=wrapper-api:latest .

        # create kubernetes resources
        kubectl apply -f wrapper-api-deployment.yaml --force=true --namespace=$project
        kubectl apply -f wrapper-api-service.yaml --namespace=$project
    fi

    if [[ $dataApi = 'true' ]]
    then
        cd "${apisPath}/REN_API/dataAPI"
        mvn clean package -Dmaven.test.skip
        cp "./target/"*.jar "${current}/docker_config_local/APIs/data-api/api.jar"

        cd "${current}/docker_config_local/APIs/data-api"
        # API INSTALLATION
        # set environment variables
        eval $(minikube docker-env)

        # delete kubernetes resources if exists
        kubectl delete deployments/data-api --namespace=$project
        kubectl delete services/data-api-sv --namespace=$project

        docker build --no-cache --force-rm --tag=data-api:latest .

        # create kubernetes resources
        kubectl apply -f data-api-deployment.yaml --force=true --namespace=$project
        kubectl apply -f data-api-service.yaml --namespace=$project
    fi
    
    if [[ $kubeflowApi = 'true' ]]
    then
        cd "${apisPath}/kubeflowAPI"
        mvn clean package -Dmaven.test.skip
        cp "./target/"*.jar "${current}/docker_config_local/APIs/kubeflow-api/api.jar"

        cd "${current}/docker_config_local/APIs/kubeflow-api"
        # API INSTALLATION
        # set environment variables
        eval $(minikube docker-env)

        # delete kubernetes resources if exists
        kubectl delete deployments/kubeflow-api --namespace=$project
        kubectl delete services/kubeflow-api-sv --namespace=$project

        docker build --no-cache --force-rm --tag=kubeflow-api:latest .

        # create kubernetes resources
        kubectl apply -f kubeflow-api-deployment.yaml --force=true --namespace=$project
        kubectl apply -f kubeflow-api-service.yaml --namespace=$project
    fi
# DEPLOY WSO2 API MANAGER

    if [[ $wso2 = 'true' ]]
    then
        cd "${current}/docker_config_local/Others/wso2"
        # WSO2 INSTALLATION
        # set environment variables
        eval $(minikube docker-env)

        # delete kubernetes resources if exists
        kubectl delete deployments/wso --namespace=$project
        kubectl delete services/wso-sv --namespace=$project

        docker build --no-cache --force-rm --tag=wso:latest .

        # create kubernetes resources
        kubectl apply -f wso2-deployment.yaml --force=true --namespace=$project
        kubectl apply -f wso2-service.yaml --namespace=$project
    fi
# DEPLOY GRAFANA

    if [[ $grafana = 'true' ]]
    then
        cd "${current}/docker_config_local/Others/grafana"
        # GRAFANA INSTALLATION
        # set environment variables
        eval $(minikube docker-env)

        # delete kubernetes resources if exists
        kubectl delete deployments/grafana --namespace=$project
        kubectl delete services/grafana-sv --namespace=$project

        docker build --no-cache --force-rm --tag=grafana:latest .

        # create kubernetes resources
        kubectl apply -f grafana-config.yaml --namespace=$project
        kubectl apply -f grafana-volume.yaml --namespace=$project
        kubectl apply -f grafana-deployment.yaml --force=true --namespace=$project
        kubectl apply -f grafana-service.yaml --namespace=$project
    fi
# DEPLOY UI AND KEYCLOAK

    if [[ $keycloak = 'true' ]]
    then
        rm -f -r "${current}/docker_config_local/Others/Keycloak/themes"
        mkdir -p "${current}/docker_config_local/Others/keycloak/themes"
        cp -f -rT "${current}/keycloak_themes" "${current}/docker_config_local/Others/keycloak/themes"

        cd "${current}/docker_config_local/Others/keycloak"
        # KEYCLOAK INSTALLATION
        # set environment variables
        eval $(minikube docker-env)

        # delete kubernetes resources if exists
        kubectl delete deployments/keycloak --namespace=$project
        kubectl delete services/keycloak-sv --namespace=$project

        docker build --no-cache --force-rm --tag=keycloak:latest .

        # create kubernetes resources
        kubectl apply -f keycloak-deployment.yaml --force=true --namespace=$project
        kubectl apply -f keycloak-service.yaml --namespace=$project
    fi


    if [[ $ui = 'true' ]]
    then
        cd "${uiPath}/renergetic_ui"
#            cp -f "${current}/Others/renergetic-ui/.env" ".env"
        npm install
        npm run build --prod
        rm -f -r "${current}/docker_config_local/Others/renergetic-uidist"
        cp -f -r "./dist" "${current}/docker_config_local/Others/renergetic-ui"
        
        cd "${current}/docker_config_local/Others/renergetic-ui"
        # FRONT INSTALLATION
        # set environment variables
        eval $(minikube docker-env)
        # prepare SSL certificates
        mkdir -p certs
        if ! [ "$(ls -A certs)" ]
        then
            cd "${current}/docker_config_local/Others/renergetic-ui/certs"
            openssl req --new --newkey rsa:4096 --x509 --sha256 --days 365 --nodes --out nginx-certificate.crt --subj '//C=ES\ST=Madrid\L=Madrid\O=Inetum\OU=IT\CN=front-ren-prototype.apps.paas-dev.psnc.pl' --keyout nginx.key
            cd "${current}/docker_config_local/Others/renergetic-ui"
        fi

        # delete kubernetes resources if exists
        kubectl delete deployments/renergetic-ui --namespace=$project
        kubectl delete services/renergetic-ui-sv --namespace=$project

        docker build --no-cache --force-rm --tag=renergetic-ui:latest .

        # create kubernetes resources
        kubectl apply -f ui-deployment.yaml --force=true --namespace=$project
        kubectl apply -f ui-service.yaml --namespace=$project
    fi

fi
