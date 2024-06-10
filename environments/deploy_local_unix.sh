current=$(pwd)


# HINT: gnu grep is required to use the -P option
apisPath=$(ggrep -ioP "(apisPath\s*=\s*)\K.+" _installers.properties)
uiPath=$(ggrep -ioP "(uiPath\s*=\s*)\K.+" _installers.properties)

[[ "$apisPath" = "default"  ]] && apisPath="${current}/../services"
[[ "$uiPath" = "default" ]] && uiPath="${current}/../front"

# Minikube namespace
project=$(ggrep -ioP "(project\s*=\s*)\K.+" _installers.properties)

# Databases
postgreSQL=$(ggrep -ioP "(postgreSQL\s*=\s*)\K.+" _installers.properties)
influxDB=$(ggrep -ioP "(influxDB\s*=\s*)\K.+" _installers.properties)
# APIs
hdr=$(ggrep -ioP "(hdr\s*=\s*)\K.+" _installers.properties)
influx=$(ggrep -ioP "(influx\s*=\s*)\K.+" _installers.properties)
ingestion=$(ggrep -ioP "(ingestion\s*=\s*)\K.+" _installers.properties)
# Others
ui=$(ggrep -ioP "(ui\s*=\s*)\K.+" _installers.properties)
keycloak=$(ggrep -ioP "(keycloak\s*=\s*)\K.+" _installers.properties)
grafana=$(ggrep -ioP "(grafana\s*=\s*)\K.+" _installers.properties)
nifi=$(ggrep -ioP "(nifi\s*=\s*)\K.+" _installers.properties)
wso2=$(ggrep -ioP "(wso2\s*=\s*)\K.+" _installers.properties)

# Connect to Minikube
if ! minikube status &> /dev/null;
then 
    minikube start --driver=docker
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
        eval $(minikube docker-env --shell bash)

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
        eval $(minikube docker-env --shell bash)

        kubectl delete deployments/influx-db --namespace=$project
        kubectl delete services/influx-db-sv --namespace=$project
        
        docker build --no-cache --force-rm --tag=influx:latest .
        
        kubectl apply -f influxdb-volume.yaml --namespace=$project
        kubectl apply -f influxdb-secrets.yaml --namespace=$project
        kubectl apply -f influxdb-deployment.yaml --namespace=$project
        kubectl apply -f influxdb-service.yaml --namespace=$project
    fi
# DEPLOY APIs

    if [[ $hdr = 'true' ]]
    then
        cd "${apisPath}/hdrAPI"
        mvn clean package -Dmaven.test.skip
        cp "./target/"*.jar "${current}/docker_config_local/APIs/hdr-api/api.jar"

        cd "${current}/docker_config_local/APIs/hdr-api"
        # API INSTALLATION
        # set environment variables
        eval $(minikube docker-env --shell bash)

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
        eval $(minikube docker-env --shell bash)

        # delete kubernetes resources if exists
        kubectl delete deployments/influx-api --namespace=$project
        kubectl delete services/influx-api-sv --namespace=$project

        docker build --no-cache --force-rm --tag=influx-api:latest .

        # create kubernetes resources
        kubectl apply -f influx-api-deployment.yaml --force=true --namespace=$project
        kubectl apply -f influx-api-service.yaml --namespace=$project
    fi

    if [[ $ingestion = 'true' ]]
    then
        cd "${apisPath}/ingestionAPI"
        mvn clean package -Dmaven.test.skip
        cp "./target/"*.jar "${current}/docker_config_local/APIs/ingestion-api/api.jar"

        cd "${current}/docker_config_local/APIs/ingestion-api"
        # API INSTALLATION
        # set environment variables
        eval $(minikube docker-env --shell bash)

        # delete kubernetes resources if exists
        kubectl delete deployments/ingestion-api --namespace=$project
        kubectl delete services/ingestion-api-sv --namespace=$project

        docker build --no-cache --force-rm --tag=ingestion-api:latest .

        # create kubernetes resources
        kubectl apply -f ingestion-api-deployment.yaml --force=true --namespace=$project
        kubectl apply -f ingestion-api-service.yaml --namespace=$project
    fi
# DEPLOY WSO2 API MANAGER

    if [[ $wso2 = 'true' ]]
    then
        cd "${current}/docker_config_local/Others/wso2"
        # WSO2 INSTALLATION
        # set environment variables
        eval $(minikube docker-env --shell bash)

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
        eval $(minikube docker-env --shell bash)

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
        cp -f -r "${current}/keycloak_themes" "${current}/docker_config_local/Others/keycloak/themes"

        cd "${current}/docker_config_local/Others/keycloak"
        # KEYCLOAK INSTALLATION
        # set environment variables
        eval $(minikube docker-env --shell bash)

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
        eval $(minikube docker-env --shell bash)

        # delete kubernetes resources if exists
        kubectl delete deployments/renergetic-ui --namespace=$project
        kubectl delete services/renergetic-ui-sv --namespace=$project

        docker build --no-cache --force-rm --tag=renergetic-ui:latest .

        # create kubernetes resources
        kubectl apply -f ui-deployment.yaml --force=true --namespace=$project
        kubectl apply -f ui-service.yaml --namespace=$project
    fi

fi
