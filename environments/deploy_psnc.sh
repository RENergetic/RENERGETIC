current=$(pwd -W tr / \\)

apisPath=$(grep -ioP "(apisPath\s*=\s*)\K.+" _installers.properties)
uiPath=$(grep -ioP "(uiPath\s*=\s*)\K.+" _installers.properties)

[[ "$apisPath" = "default"  ]] && apisPath="${current}/../services"
[[ "$uiPath" = "default" ]] && uiPath="${current}/../front"

serverUrl=$(grep -ioP "(server\s*=\s*)\K.+" _credentials.properties)

# Project name
project=$(grep -ioP "(project\s*=\s*)\K.+" _installers.properties)
export PROJECT=$project

# Credentials
user=$(grep -ioP "(user\s*=\s*)\K.+" _credentials.properties)
token=$(grep -ioP "(token\s*=\s*)\K.+" _credentials.properties)

# Databases
postgreSQL=$(grep -ioP "(postgreSQL\s*=\s*)\K.+" _installers.properties)
influxDB=$(grep -ioP "(influxDB\s*=\s*)\K.+" _installers.properties)
# APIs
hdr=$(grep -ioP "(hdr\s*=\s*)\K.+" _installers.properties)
kpi=$(grep -ioP "(kpi\s*=\s*)\K.+" _installers.properties)
influx=$(grep -ioP "(influx\s*=\s*)\K.+" _installers.properties)
ingestion=$(grep -ioP "(ingestion\s*=\s*)\K.+" _installers.properties)
# Others
ui=$(grep -ioP "(ui\s*=\s*)\K.+" _installers.properties)
keycloak=$(grep -ioP "(keycloak\s*=\s*)\K.+" _installers.properties)
grafana=$(grep -ioP "(grafana\s*=\s*)\K.+" _installers.properties)
nifi=$(grep -ioP "(nifi\s*=\s*)\K.+" _installers.properties)
wso2=$(grep -ioP "(wso2\s*=\s*)\K.+" _installers.properties)
nexus=$(grep -ioP "(nexus\s*=\s*)\K.+" _installers.properties)

rm -rf ~/.kube

# Connect to PSNC server and log in at Docker
if oc login $serverUrl --token=$token;
then
    oc project $project

# DEPLOY DATABASES

    if [[ $postgreSQL = 'true' ]]
    then
        cd  "${current}/docker_config/Databases/postgresql"
        # POSTGRESQL INSTALLATION
        # set environment variables

        kubectl delete configmaps/postgresql-db-config
        kubectl delete statefulsets/postgresql-db
        kubectl delete services/postgresql-db-sv
        
        docker build --no-cache --force-rm --tag=registry.apps.paas-dev.psnc.pl/$project/postgresql-db:latest .
        docker login -u $user -p $token https://registry.apps.paas-dev.psnc.pl/
        docker push registry.apps.paas-dev.psnc.pl/$project/postgresql-db:latest
        
        kubectl apply -f postgresql-configmap.yaml
        envsubst '$PROJECT' < postgresql-statefulset.yaml | kubectl apply -f -
        kubectl apply -f postgresql-service.yaml
        
        while [[ $(kubectl -n ${project} get pods -l app=postgresql-db -o 'jsonpath={..status.conditions[?(@.type=="Ready")].status}') != "True" ]]; 
            do echo "Waiting to PostgreSQL pod to create databases" && sleep 5; 
        done
        kubectl exec statefulset/postgresql-db --namespace=$project -- bin/bash -c "psql -U postgres < ./scripts/init.sql"
    fi

    if [[ $influxDB = 'true' ]]
    then
        cd  "${current}/docker_config/Databases/influxdb"
        # INFLUXDB INSTALLATION
        # set environment variables

        kubectl delete deployments/influx-db
        kubectl delete services/influx-db-sv
        
        docker build --no-cache --force-rm --tag=registry.apps.paas-dev.psnc.pl/$project/influx:latest .
        docker login -u $user -p $token https://registry.apps.paas-dev.psnc.pl/
        docker push registry.apps.paas-dev.psnc.pl/$project/influx:latest
        
        kubectl apply -f influxdb-volume.yaml
        kubectl apply -f influxdb-secrets.yaml
        envsubst '$PROJECT' < influxdb-deployment.yaml | kubectl apply -f -
        kubectl apply -f influxdb-service.yaml
    fi
# DEPLOY APIs

    if [[ $hdr = 'true' ]]
    then
        cd "${apisPath}/hdrAPI"
        mvn clean package -Dmaven.test.skip
        cp "./target/"*.jar "${current}/docker_config/APIs/hdr-api/api.jar"

        cd "${current}/docker_config/APIs/hdr-api"
        # API INSTALLATION
        # set environment variables

        # delete kubernetes resources if exists
        kubectl delete deployments/hdr-api
        kubectl delete services/hdr-api-sv

        docker build --no-cache --force-rm --tag=registry.apps.paas-dev.psnc.pl/$project/hdr-api:latest .
        docker login -u $user -p $token https://registry.apps.paas-dev.psnc.pl/
        docker push registry.apps.paas-dev.psnc.pl/$project/hdr-api:latest

        # create kubernetes resources
        envsubst '$PROJECT' < hdr-api-deployment.yaml | kubectl apply --force=true -f -
        kubectl apply -f hdr-api-service.yaml
    fi

    if [[ $influx = 'true' ]]
    then
        cd "${apisPath}/measurementapi"
        mvn clean package -Dmaven.test.skip
        cp "./target/"*.jar "${current}/docker_config/APIs/influx-api/api.jar"

        cd "${current}/docker_config/APIs/influx-api"
        # API INSTALLATION
        # set environment variables

        # delete kubernetes resources if exists
        kubectl delete deployments/influx-api
        kubectl delete services/influx-api-sv

        docker build --no-cache --force-rm --tag=registry.apps.paas-dev.psnc.pl/$project/influx-api:latest .
        docker login -u $user -p $token https://registry.apps.paas-dev.psnc.pl/
        docker push registry.apps.paas-dev.psnc.pl/$project/influx-api:latest

        # create kubernetes resources
        envsubst '$PROJECT' < influx-api-deployment.yaml | kubectl apply --force=true -f -
        kubectl apply -f influx-api-service.yaml
    fi

    if [[ $kpi = 'true' ]]
    then
        cd "${apisPath}/kpiAPI"
        mvn clean package -Dmaven.test.skip
        cp "./target/"*.jar "${current}/docker_config/APIs/kpi-api/api.jar"

        cd "${current}/docker_config/APIs/kpi-api"
        # API INSTALLATION
        # set environment variables

        # delete kubernetes resources if exists
        kubectl delete deployments/kpi-api
        kubectl delete services/kpi-api-sv

        docker build --no-cache --force-rm --tag=registry.apps.paas-dev.psnc.pl/$project/kpi-api:latest .
        docker login -u $user -p $token https://registry.apps.paas-dev.psnc.pl/
        docker push registry.apps.paas-dev.psnc.pl/$project/kpi-api:latest

        # create kubernetes resources
        envsubst '$PROJECT' < kpi-api-deployment.yaml | kubectl apply --force=true -f -
        kubectl apply -f kpi-api-service.yaml
    fi

    if [[ $ingestion = 'true' ]]
    then
        cd "${apisPath}/ingestionAPI"
        mvn clean package -Dmaven.test.skip
        cp "./target/"*.jar "${current}/docker_config/APIs/ingestion-api/api.jar"

        cd "${current}/docker_config/APIs/ingestion-api"
        # API INSTALLATION
        # set environment variables

        # delete kubernetes resources if exists
        kubectl delete deployments/ingestion-api
        kubectl delete services/ingestion-api-sv

        docker build --no-cache --force-rm --tag=registry.apps.paas-dev.psnc.pl/$project/ingestion-api:latest .
        docker login -u $user -p $token https://registry.apps.paas-dev.psnc.pl/
        docker push registry.apps.paas-dev.psnc.pl/$project/ingestion-api:latest

        # create kubernetes resources
        envsubst '$PROJECT' < ingestion-api-deployment.yaml | kubectl apply --force=true -f -
        kubectl apply -f ingestion-api-service.yaml
    fi
# DEPLOY WSO2 API MANAGER

    if [[ $wso2 = 'true' ]]
    then
        cd "${current}/docker_config/Others/wso2"
        # WSO2 INSTALLATION
        # set environment variables

        # delete kubernetes resources if exists
        kubectl delete deployments/wso
        kubectl delete services/wso-sv

        docker build --no-cache --force-rm --tag=registry.apps.paas-dev.psnc.pl/$project/wso:latest .
        docker login -u $user -p $token https://registry.apps.paas-dev.psnc.pl/
        docker push registry.apps.paas-dev.psnc.pl/$project/wso:latest

        # create kubernetes resources
        envsubst '$PROJECT' < wso2-deployment.yaml | kubectl apply --force=true -f -
        kubectl apply -f wso2-service.yaml
    fi
# DEPLOY NEXUS API MANAGER

    if [[ $nexus = 'true' ]]
    then
        cd "${current}/docker_config/Devops/nexus"
        # NEXUS INSTALLATION
        # set environment variables

        # delete kubernetes resources if exists
        kubectl delete statefulsets/nexus --namespace=ren-prototype-devops
        kubectl delete services/nexus-sv --namespace=ren-prototype-devops

        docker build --no-cache --force-rm --tag=registry.apps.paas-dev.psnc.pl/ren-prototype-devops/nexus:latest .
        docker login -u $user -p $token https://registry.apps.paas-dev.psnc.pl/
        docker push registry.apps.paas-dev.psnc.pl/ren-prototype-devops/nexus:latest

        # create kubernetes resources
        kubectl apply --force=true -f nexus-statefulset.yaml --namespace=ren-prototype-devops
        kubectl apply -f nexus-service.yaml --namespace=ren-prototype-devops
    fi
# DEPLOY GRAFANA

    if [[ $grafana = 'true' ]]
    then
        cd "${current}/docker_config/Others/grafana"
        # GRAFANA INSTALLATION
        # set environment variables

        # delete kubernetes resources if exists
        kubectl delete deployments/grafana
        kubectl delete services/grafana-sv

        docker build --no-cache --force-rm --tag=registry.apps.paas-dev.psnc.pl/$project/grafana:latest .
        docker login -u $user -p $token https://registry.apps.paas-dev.psnc.pl/
        docker push registry.apps.paas-dev.psnc.pl/$project/grafana:latest

        # create kubernetes resources
        kubectl apply -f grafana-config.yaml
        kubectl apply -f grafana-volume.yaml
        envsubst '$PROJECT' < grafana-deployment.yaml | kubectl apply --force=true -f -
        kubectl apply -f grafana-service.yaml
    fi
# DEPLOY UI AND KEYCLOAK

    if [[ $keycloak = 'true' ]]
    then
        rm -f -r "${current}/docker_config/Others/keycloak/themes"
        mkdir -p "${current}/docker_config/Others/keycloak/themes"
        cp -f -rT "${current}/keycloak_themes/" "${current}/docker_config/Others/keycloak/themes"

        cd "${current}/docker_config/Others/keycloak"
        # KEYCLOAK INSTALLATION
        # set environment variables

        # delete kubernetes resources if exists
        kubectl delete deployments/keycloak
        kubectl delete services/keycloak-sv

        docker build --no-cache --force-rm --tag=registry.apps.paas-dev.psnc.pl/$project/keycloak:latest .
        docker login -u $user -p $token https://registry.apps.paas-dev.psnc.pl/
        docker push registry.apps.paas-dev.psnc.pl/$project/keycloak:latest

        # create kubernetes resources
        envsubst '$PROJECT' < keycloak-deployment.yaml | kubectl apply --force=true -f -
        kubectl apply -f keycloak-service.yaml
    fi


    if [[ $ui = 'true' ]]
    then
        cd "${uiPath}/renergetic_ui"
#            cp -f "${current}/Others/renergetic-ui/.env" ".env"
        npm install
        npm run build --prod
        rm -f -r "${current}/docker_config/Others/renergetic-uidist"
        cp -f -r "./dist" "${current}/docker_config/Others/renergetic-ui"
        
        cd "${current}/docker_config/Others/renergetic-ui"
        # FRONT INSTALLATION
        # prepare SSL certificates
        mkdir -p certs
        if ! [ "$(ls -A certs)" ]
        then
            cd "${current}/docker_config/Others/renergetic-ui/certs"
            openssl req --new --newkey rsa:4096 --x509 --sha256 --days 365 --nodes --out nginx-certificate.crt --subj '//C=ES\ST=Madrid\L=Madrid\O=Inetum\OU=IT\CN=front-ren-prototype.apps.paas-dev.psnc.pl' --keyout nginx.key
            cd "${current}/docker_config/Others/renergetic-ui"
        fi
        
        delete kubernetes resources if exists
        kubectl delete deployments/renergetic-ui
        kubectl delete services/renergetic-ui-sv

        docker build --no-cache --force-rm --tag=registry.apps.paas-dev.psnc.pl/$project/renergetic-ui:latest .
        docker login -u $user -p $token https://registry.apps.paas-dev.psnc.pl/
        docker push registry.apps.paas-dev.psnc.pl/$project/renergetic-ui:latest

        # create kubernetes resources
        envsubst '$PROJECT' < ui-deployment.yaml | kubectl apply --force=true -f -
        kubectl apply -f ui-service.yaml
    fi

fi