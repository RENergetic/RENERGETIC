current=$(pwd -W tr / \\)

apisPath="${current}/../services"
uiPath="${current}/../front"

# Project name
project = $(grep -ioP "(project\s*=\s*)\K.+" _installers.properties)

# Credentials
user = $(grep -ioP "(user\s*=\s*)\K.+" _credentials.properties)
token = $(grep -ioP "(token\s*=\s*)\K.+" _credentials.properties)

# Databases
postgreSQL = $(grep -ioP "(postgreSQL\s*=\s*)\K.+" _installers.properties)
influxDB = $(grep -ioP "(influxDB\s*=\s*)\K.+" _installers.properties)
# APIs
hdr = $(grep -ioP "(hdr\s*=\s*)\K.+" _installers.properties)
influx = $(grep -ioP "(influx\s*=\s*)\K.+" _installers.properties)
ingestion = $(grep -ioP "(ingestion\s*=\s*)\K.+" _installers.properties)
# Others
ui = $(grep -ioP "(ui\s*=\s*)\K.+" _installers.properties)
keycloak=$(grep -ioP "(keycloak\s*=\s*)\K.+" _installers.properties)
grafana = $(grep -ioP "(grafana\s*=\s*)\K.+" _installers.properties)
nifi = $(grep -ioP "(nifi\s*=\s*)\K.+" _installers.properties)
wso2 = $(grep -ioP "(wso2\s*=\s*)\K.+" _installers.properties)

# Connect to PSNC server and log in at Docker
if oc login https://console.paas-dev.psnc.pl --token=$token;
then
    oc project $project

    if docker login -u $user -p $token https://registry.apps.paas-dev.psnc.pl/;
    then
    # DEPLOY DATABASES

        if [[ $postgreSQL = 'true' ]]
        then
            cd  "${current}/docker_config/Databases/postgresql"
            # POSTGRESQL INSTALLATION
            # set environment variables
            eval $(minikube docker-env)

            kubectl delete configmaps/postgresql-db-config
            kubectl delete statefulsets/postgresql-db
            kubectl delete services/postgresql-db-sv
            
            docker build --no-cache --force-rm --tag=registry.apps.paas-dev.psnc.pl/$project/postgresql-db:latest .
            docker push registry.apps.paas-dev.psnc.pl/$project/postgresql-db:latest
            
            kubectl apply -f postgresql-configmap.yaml
            kubectl apply -f postgresql-statefulset.yaml
            kubectl apply -f postgresql-service.yaml
        fi

        if [[ $influxDB = 'true' ]]
        then
            cd  "${current}/docker_config/Databases/influxdb"
            # INFLUXDB INSTALLATION
            # set environment variables
            eval $(minikube docker-env)

            kubectl delete deployments/influx-db
            kubectl delete services/influx-db-sv
            
            docker build --no-cache --force-rm --tag=registry.apps.paas-dev.psnc.pl/$project/influx:latest .
            docker push registry.apps.paas-dev.psnc.pl/$project/influx:latest
            
            kubectl apply -f influxdb-volume.yaml
            kubectl apply -f influxdb-secrets.yaml
            kubectl apply -f influxdb-deployment.yaml
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
            eval $(minikube docker-env)

            # delete kubernetes resources if exists
            kubectl delete deployments/hdr-api
            kubectl delete services/hdr-api-sv

            docker build --no-cache --force-rm --tag=registry.apps.paas-dev.psnc.pl/$project/hdr-api:latest .
            docker push registry.apps.paas-dev.psnc.pl/$project/hdr-api:latest

            # create kubernetes resources
            kubectl apply -f hdr-api-deployment.yaml --force=true
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
            eval $(minikube docker-env)

            # delete kubernetes resources if exists
            kubectl delete deployments/influx-api
            kubectl delete services/influx-api-sv

            docker build --no-cache --force-rm --tag=registry.apps.paas-dev.psnc.pl/$project/influx-api:latest .
            docker push registry.apps.paas-dev.psnc.pl/$project/influx-api:latest

            # create kubernetes resources
            kubectl apply -f influx-api-deployment.yaml --force=true
            kubectl apply -f influx-api-service.yaml
        fi
    
        if [[ $ingestion = 'true' ]]
        then
            cd "${apisPath}/ingestionAPI"
            mvn clean package -Dmaven.test.skip
            cp "./target/"*.jar "${current}/docker_config/APIs/ingestion-api/api.jar"

            cd "${current}/docker_config/APIs/ingestion-api"
            # API INSTALLATION
            # set environment variables
            eval $(minikube docker-env)

            # delete kubernetes resources if exists
            kubectl delete deployments/ingestion-api
            kubectl delete services/ingestion-api-sv

            docker build --no-cache --force-rm --tag=registry.apps.paas-dev.psnc.pl/$project/ingestion-api:latest .
            docker push registry.apps.paas-dev.psnc.pl/$project/ingestion-api:latest

            # create kubernetes resources
            kubectl apply -f ingestion-api-deployment.yaml --force=true
            kubectl apply -f ingestion-api-service.yaml
        fi
    # DEPLOY WSO2 API MANAGER
    
        if [[ $wso2 = 'true' ]]
        then
            cd "${current}/docker_config/Others/wso2"
            # WSO2 INSTALLATION
            # set environment variables
            eval $(minikube docker-env)

            # delete kubernetes resources if exists
            kubectl delete deployments/wso
            kubectl delete services/wso-sv

            docker build --no-cache --force-rm --tag=registry.apps.paas-dev.psnc.pl/$project/wso:latest .
            docker push registry.apps.paas-dev.psnc.pl/$project/wso:latest

            # create kubernetes resources
            kubectl apply -f wso2-deployment.yaml --force=true
            kubectl apply -f wso2-service.yaml
        fi
    # DEPLOY GRAFANA
    
        if [[ $grafana = 'true' ]]
        then
            cd "${current}/docker_config/Others/grafana"
            # GRAFANA INSTALLATION
            # set environment variables
            eval $(minikube docker-env)

            # delete kubernetes resources if exists
            kubectl delete deployments/grafana
            kubectl delete services/grafana-sv

            docker build --no-cache --force-rm --tag=registry.apps.paas-dev.psnc.pl/$project/grafana:latest .
            docker push registry.apps.paas-dev.psnc.pl/$project/grafana:latest

            # create kubernetes resources
            kubectl apply -f grafana-config.yaml
            kubectl apply -f grafana-volume.yaml
            kubectl apply -f grafana-deployment.yaml --force=true
            kubectl apply -f grafana-service.yaml
        fi
    # DEPLOY UI AND KEYCLOAK

        if [[ $keycloak = 'true' ]]
        then
            rm -f -r "${current}/docker_config/Others/Keycloak/themes"
            mkdir -p "${current}/docker_config/Others/keycloak/themes"
            cp -f -r "${current}/keycloak_themes/" "${current}/docker_config/Others/keycloak/themes"

            cd "${current}/docker_config/Others/keycloak"
            # KEYCLOAK INSTALLATION
            # set environment variables
            eval $(minikube docker-env)

            # delete kubernetes resources if exists
            kubectl delete deployments/keycloak
            kubectl delete services/keycloak-sv

            docker build --no-cache --force-rm --tag=registry.apps.paas-dev.psnc.pl/$project/keycloak:latest .
            docker push registry.apps.paas-dev.psnc.pl/$project/keycloak:latest

            # create kubernetes resources
            kubectl apply -f keycloak-deployment.yaml --force=true
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
            # set environment variables
            eval $(minikube docker-env)

            # delete kubernetes resources if exists
            kubectl delete deployments/renergetic-ui
            kubectl delete services/renergetic-ui-sv

            docker build --no-cache --force-rm --tag=registry.apps.paas-dev.psnc.pl/$project/renergetic-ui:latest .
            docker push registry.apps.paas-dev.psnc.pl/$project/renergetic-ui:latest

            # create kubernetes resources
            kubectl apply -f renergetic-ui-deployment.yaml --force=true
            kubectl apply -f renergetic-ui-service.yaml
        fi

    fi

fi