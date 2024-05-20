current=$(pwd -W tr / \\)

apisPath=$(grep -ioP "(apisPath\s*=\s*)\K.+" _installers.properties)
uiPath=$(grep -ioP "(uiPath\s*=\s*)\K.+" _installers.properties)

[[ "$apisPath" = "default"  ]] && apisPath="${current}/../services"
[[ "$uiPath" = "default" ]] && uiPath="${current}/../front"

serverUrl=$(grep -ioP "(server\s*=\s*)\K.+" _credentials.properties)

# Project name
_project=$(grep -ioP "(project\s*=\s*)\K.+" _installers.properties)

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
rules=$(grep -ioP "(rules\s*=\s*)\K.+" _installers.properties)
baseApi=$(grep -ioP "(base\s*=\s*)\K.+" _installers.properties)
userApi=$(grep -ioP "(user\s*=\s*)\K.+" _installers.properties)
wrapperApi=$(grep -ioP "(wrapper\s*=\s*)\K.+" _installers.properties)
dataApi=$(grep -ioP "(data\s*=\s*)\K.+" _installers.properties)
kubeflowApi=$(grep -ioP "(kubeflow\s*=\s*)\K.+" _installers.properties)
# Others
ui=$(grep -ioP "(ui\s*=\s*)\K.+" _installers.properties)
keycloak=$(grep -ioP "(keycloak\s*=\s*)\K.+" _installers.properties)
grafana=$(grep -ioP "(grafana\s*=\s*)\K.+" _installers.properties)
nifi=$(grep -ioP "(nifi\s*=\s*)\K.+" _installers.properties)
krakend=$(grep -ioP "(krakend\s*=\s*)\K.+" _installers.properties)
nexus=$(grep -ioP "(nexus\s*=\s*)\K.+" _installers.properties)

resetConnection() {
    if oc whoami;
    then
        return 0;
    else
        return oc login $serverUrl --token=$token;
    fi
}

compileApp() {
    if [[ $baseApi = 'true' ]]
    then
        cd "${apisPath}/REN_API/baseAPI"
        mvn clean package -Dmaven.test.skip
        cp "./target/"*.jar "${current}/docker_config/APIs/base-api/api.jar"
    fi

    if [[ $hdr = 'true' ]]
    then
        cd "${apisPath}/hdrAPI"
        mvn clean package -Dmaven.test.skip
        cp "./target/"*.jar "${current}/docker_config/APIs/hdr-api/api.jar"
    fi

    if [[ $influx = 'true' ]]
    then
        cd "${apisPath}/measurementapi"
        mvn clean package -Dmaven.test.skip
        cp "./target/"*.jar "${current}/docker_config/APIs/influx-api/api.jar"
    fi

    if [[ $kpi = 'true' ]]
    then
        cd "${apisPath}/kpiAPI"
        mvn clean package -Dmaven.test.skip
        cp "./target/"*.jar "${current}/docker_config/APIs/kpi-api/api.jar"
    fi

    if [[ $ingestion = 'true' ]]
    then
        cd "${apisPath}/ingestionAPI"
        mvn clean package -Dmaven.test.skip
        cp "./target/"*.jar "${current}/docker_config/APIs/ingestion-api/api.jar"
    fi

    if [[ $rules = 'true' ]]
    then
        cd "${apisPath}/ruleEvaluationService"
        mvn clean package -Dmaven.test.skip
        cp "./target/"*.jar "${current}/docker_config/APIs/rules-api/api.jar"
    fi

    if [[ $userApi = 'true' ]]
    then
        cd "${apisPath}/REN_API/userAPI"
        mvn clean package -Dmaven.test.skip
        cp "./target/"*.jar "${current}/docker_config/APIs/user-api/api.jar"
    fi

    if [[ $wrapperApi = 'true' ]]
    then
        cd "${apisPath}/REN_API/wrapperAPI"
        mvn clean package -Dmaven.test.skip
        cp "./target/"*.jar "${current}/docker_config/APIs/wrapper-api/api.jar"
    fi

    if [[ $dataApi = 'true' ]]
    then
        cd "${apisPath}/REN_API/dataAPI"
        mvn clean package -Dmaven.test.skip
        cp "./target/"*.jar "${current}/docker_config/APIs/data-api/api.jar"
    fi
    
    if [[ $kubeflowApi = 'true' ]]
    then
        cd "${apisPath}/kubeflowAPI"
        mvn clean package -Dmaven.test.skip
        cp "./target/"*.jar "${current}/docker_config/APIs/kubeflow-api/api.jar"
    fi

    if [[ $ui = 'true' ]]
    then
        cd "${uiPath}/renergetic_ui"
        # cp -f "${current}/Others/renergetic-ui/.env" ".env"
        npm install
        npm run build --prod
        rm -f -r "${current}/docker_config/Others/renergetic-ui/dist"
        cp -f -r "./dist" "${current}/docker_config/Others/renergetic-ui"
    fi
}

installPSNC() {
# DEPLOY DATABASES

    if [[ $postgreSQL = 'true' ]]
    then
        cd  "${current}/docker_config/Databases/postgresql"
        # POSTGRESQL INSTALLATION
        # set environment variables

        kubectl delete configmaps/postgresql-db-config --namespace=$project
        kubectl delete statefulsets/postgresql-db --namespace=$project
        kubectl delete services/postgresql-db-sv --namespace=$project
        
        docker build --no-cache --force-rm --tag=registry.apps.paas-dev.psnc.pl/$project/postgresql-db:latest .
        docker login -u $user -p $token https://registry.apps.paas-dev.psnc.pl/
        docker push registry.apps.paas-dev.psnc.pl/$project/postgresql-db:latest
        
        kubectl apply -f postgresql-configmap.yaml --namespace=$project
        envsubst '$PROJECT' < postgresql-statefulset.yaml | kubectl apply --namespace=$project -f -
        kubectl apply -f postgresql-service.yaml --namespace=$project
        
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

        kubectl delete deployments/influx-db --namespace=$project
        kubectl delete services/influx-db-sv --namespace=$project
        kubectl delete secrets/influxdb-secrets --namespace=$project
        
        docker build --no-cache --force-rm --tag=registry.apps.paas-dev.psnc.pl/$project/influx:latest .
        docker login -u $user -p $token https://registry.apps.paas-dev.psnc.pl/
        docker push registry.apps.paas-dev.psnc.pl/$project/influx:latest
        
        kubectl apply -f influxdb-volume.yaml --namespace=$project
        kubectl apply -f influxdb-secrets.yaml --namespace=$project
        envsubst '$PROJECT' < influxdb-deployment.yaml | kubectl apply --namespace=$project -f -
        kubectl apply -f influxdb-service.yaml --namespace=$project
    fi
# DEPLOY 

    if [[ $baseApi = 'true' ]]
    then
        cd "${current}/docker_config/APIs/base-api"
        # API INSTALLATION
        # set environment variables

        # delete kubernetes resources if exists
        kubectl delete deployments/base-api --namespace=$project
        kubectl delete services/base-api-sv --namespace=$project

        docker build --no-cache --force-rm --tag=registry.apps.paas-dev.psnc.pl/$project/base-api:latest .
        docker login -u $user -p $token https://registry.apps.paas-dev.psnc.pl/
        docker push registry.apps.paas-dev.psnc.pl/$project/base-api:latest

        # create kubernetes resources
        envsubst '$PROJECT' < base-api-deployment.yaml | kubectl apply --namespace=$project -f -
        kubectl apply -f base-api-service.yaml --namespace=$project
    fi

    if [[ $hdr = 'true' ]]
    then
        cd "${current}/docker_config/APIs/hdr-api"
        # API INSTALLATION
        # set environment variables

        # delete kubernetes resources if exists
        kubectl delete deployments/hdr-api --namespace=$project
        kubectl delete services/hdr-api-sv --namespace=$project

        docker build --no-cache --force-rm --tag=registry.apps.paas-dev.psnc.pl/$project/hdr-api:latest .
        docker login -u $user -p $token https://registry.apps.paas-dev.psnc.pl/
        docker push registry.apps.paas-dev.psnc.pl/$project/hdr-api:latest

        # create kubernetes resources
        envsubst '$PROJECT' < hdr-api-deployment.yaml | kubectl apply --namespace=$project -f -
        kubectl apply -f hdr-api-service.yaml --namespace=$project
    fi

    if [[ $influx = 'true' ]]
    then
        cd "${current}/docker_config/APIs/influx-api"
        # API INSTALLATION
        # set environment variables

        # delete kubernetes resources if exists
        kubectl delete deployments/influx-api --namespace=$project
        kubectl delete services/influx-api-sv --namespace=$project

        docker build --no-cache --force-rm --tag=registry.apps.paas-dev.psnc.pl/$project/influx-api:latest .
        docker login -u $user -p $token https://registry.apps.paas-dev.psnc.pl/
        docker push registry.apps.paas-dev.psnc.pl/$project/influx-api:latest

        # create kubernetes resources
        envsubst '$PROJECT' < influx-api-deployment.yaml | kubectl apply --namespace=$project -f -
        kubectl apply -f influx-api-service.yaml --namespace=$project
    fi

    if [[ $kpi = 'true' ]]
    then
        cd "${current}/docker_config/APIs/kpi-api"
        # API INSTALLATION
        # set environment variables

        # delete kubernetes resources if exists
        kubectl delete deployments/kpi-api --namespace=$project
        kubectl delete services/kpi-api-sv --namespace=$project

        docker build --no-cache --force-rm --tag=registry.apps.paas-dev.psnc.pl/$project/kpi-api:latest .
        docker login -u $user -p $token https://registry.apps.paas-dev.psnc.pl/
        docker push registry.apps.paas-dev.psnc.pl/$project/kpi-api:latest

        # create kubernetes resources
        envsubst '$PROJECT' < kpi-api-deployment.yaml | kubectl apply --namespace=$project -f -
        kubectl apply -f kpi-api-service.yaml --namespace=$project
    fi

    if [[ $ingestion = 'true' ]]
    then
        cd "${current}/docker_config/APIs/ingestion-api"
        # API INSTALLATION
        # set environment variables

        # delete kubernetes resources if exists
        kubectl delete deployments/ingestion-api --namespace=$project
        kubectl delete services/ingestion-api-sv --namespace=$project

        docker build --no-cache --force-rm --tag=registry.apps.paas-dev.psnc.pl/$project/ingestion-api:latest .
        docker login -u $user -p $token https://registry.apps.paas-dev.psnc.pl/
        docker push registry.apps.paas-dev.psnc.pl/$project/ingestion-api:latest

        # create kubernetes resources
        envsubst '$PROJECT' < ingestion-api-deployment.yaml | kubectl apply --namespace=$project -f -
        kubectl apply -f ingestion-api-service.yaml --namespace=$project
    fi

    if [[ $rules = 'true' ]]
    then
        cd "${current}/docker_config/APIs/rules-api"
        # API INSTALLATION
        # set environment variables

        # delete kubernetes resources if exists
        kubectl delete deployments/rules-api --namespace=$project
        kubectl delete services/rules-api-sv --namespace=$project

        docker build --no-cache --force-rm --tag=registry.apps.paas-dev.psnc.pl/$project/rules-api:latest .
        docker login -u $user -p $token https://registry.apps.paas-dev.psnc.pl/
        docker push registry.apps.paas-dev.psnc.pl/$project/rules-api:latest

        # create kubernetes resources
        envsubst '$PROJECT' < rules-api-deployment.yaml | kubectl apply --namespace=$project -f -
        kubectl apply -f rules-api-service.yaml --namespace=$project
    fi

    if [[ $userApi = 'true' ]]
    then
        cd "${current}/docker_config/APIs/user-api"
    
        # API INSTALLATION
        # set environment variables

        # delete kubernetes resources if exists
        kubectl delete deployments/user-api --namespace=$project
        kubectl delete services/user-api-sv --namespace=$project

        docker build --no-cache --force-rm --tag=registry.apps.paas-dev.psnc.pl/$project/user-api:latest .
        docker login -u $user -p $token https://registry.apps.paas-dev.psnc.pl/
        docker push registry.apps.paas-dev.psnc.pl/$project/user-api:latest

        # create kubernetes resources
        envsubst '$PROJECT' < user-api-deployment.yaml | kubectl apply --namespace=$project -f -
        kubectl apply -f user-api-service.yaml --namespace=$project
    fi

    if [[ $wrapperApi = 'true' ]]
    then
        cd "${current}/docker_config/APIs/wrapper-api"
        # API INSTALLATION
        # set environment variables

        # delete kubernetes resources if exists
        kubectl delete deployments/wrapper-api --namespace=$project
        kubectl delete services/wrapper-api-sv --namespace=$project

        docker build --no-cache --force-rm --tag=registry.apps.paas-dev.psnc.pl/$project/wrapper-api:latest .
        docker login -u $user -p $token https://registry.apps.paas-dev.psnc.pl/
        docker push registry.apps.paas-dev.psnc.pl/$project/wrapper-api:latest

        # create kubernetes resources
        envsubst '$PROJECT' < wrapper-api-deployment.yaml | kubectl apply --namespace=$project -f -
        kubectl apply -f wrapper-api-service.yaml --namespace=$project
    fi

    if [[ $dataApi = 'true' ]]
    then
        cd "${current}/docker_config/APIs/data-api"
        # API INSTALLATION
        # set environment variables

        # delete kubernetes resources if exists
        kubectl delete deployments/data-api --namespace=$project
        kubectl delete services/data-api-sv --namespace=$project

        docker build --no-cache --force-rm --tag=registry.apps.paas-dev.psnc.pl/$project/data-api:latest .
        docker login -u $user -p $token https://registry.apps.paas-dev.psnc.pl/
        docker push registry.apps.paas-dev.psnc.pl/$project/data-api:latest

        # create kubernetes resources
        envsubst '$PROJECT' < data-api-deployment.yaml | kubectl apply --namespace=$project -f -
        kubectl apply -f data-api-service.yaml --namespace=$project
    fi
    
    if [[ $kubeflowApi = 'true' ]]
    then
        cd "${current}/docker_config/APIs/kubeflow-api"
        # API INSTALLATION
        # set environment variables

        # delete kubernetes resources if exists
        kubectl delete secrets/kubeflow-api-secrets --namespace=$project
        kubectl delete deployments/kubeflow-api --namespace=$project
        kubectl delete services/kubeflow-api-sv --namespace=$project

        docker build --no-cache --force-rm --tag=registry.apps.paas-dev.psnc.pl/$project/kubeflow-api:latest .
        docker login -u $user -p $token https://registry.apps.paas-dev.psnc.pl/
        docker push registry.apps.paas-dev.psnc.pl/$project/kubeflow-api:latest

        # create kubernetes resources
        kubectl apply -f kubeflow-api-secrets.yaml --namespace=$project
        envsubst '$PROJECT' < kubeflow-api-deployment.yaml | kubectl apply --namespace=$project -f -
        kubectl apply -f kubeflow-api-service.yaml --namespace=$project
    fi

# DEPLOY krakend API MANAGER

    if [[ $krakend = 'true' ]]
    then
        cd "${current}/docker_config/Others/krakend"
        # KRAKEND INSTALLATION
        # set environment variables

        # delete kubernetes resources if exists
        kubectl apply -f configmaps/krakend-config --namespace=$project
        kubectl delete deployments/wso --namespace=$project
        kubectl delete services/wso-sv --namespace=$project

        docker build --no-cache --force-rm --tag=registry.apps.paas-dev.psnc.pl/$project/wso:latest .
        docker login -u $user -p $token https://registry.apps.paas-dev.psnc.pl/
        docker push registry.apps.paas-dev.psnc.pl/$project/krakend:latest

        # create kubernetes resources
        kubectl apply -f krakend-config.yaml --namespace=$project
        envsubst '$PROJECT' < krakend-deployment.yaml | kubectl apply --namespace=$project -f -
        kubectl apply -f krakend-service.yaml --namespace=$project
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
        kubectl delete deployments/grafana --namespace=$project
        kubectl delete services/grafana-sv --namespace=$project

        docker build --no-cache --force-rm --tag=registry.apps.paas-dev.psnc.pl/$project/grafana:latest .
        docker login -u $user -p $token https://registry.apps.paas-dev.psnc.pl/
        docker push registry.apps.paas-dev.psnc.pl/$project/grafana:latest

        # create kubernetes resources
        kubectl apply -f grafana-config.yaml --namespace=$project
        kubectl apply -f grafana-volume.yaml --namespace=$project
        envsubst '$PROJECT' < grafana-deployment.yaml | kubectl apply --namespace=$project -f -
        kubectl apply -f grafana-service.yaml --namespace=$project
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
        kubectl delete deployments/keycloak --namespace=$project
        kubectl delete services/keycloak-sv --namespace=$project

        docker build --no-cache --force-rm --tag=registry.apps.paas-dev.psnc.pl/$project/keycloak:latest .
        docker login -u $user -p $token https://registry.apps.paas-dev.psnc.pl/
        docker push registry.apps.paas-dev.psnc.pl/$project/keycloak:latest

        # create kubernetes resources
        envsubst '$PROJECT' < keycloak-deployment.yaml | kubectl apply --namespace=$project -f -
        kubectl apply -f keycloak-service.yaml --namespace=$project
    fi

    if [[ $ui = 'true' ]]
    then        
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
        
        # delete kubernetes resources if exists
        kubectl delete deployments/renergetic-ui --namespace=$project
        kubectl delete services/renergetic-ui-sv --namespace=$project

        docker build --no-cache --force-rm --tag=registry.apps.paas-dev.psnc.pl/$project/renergetic-ui:latest .
        docker login -u $user -p $token https://registry.apps.paas-dev.psnc.pl/
        docker push registry.apps.paas-dev.psnc.pl/$project/renergetic-ui:latest

        # create kubernetes resources
        envsubst '$PROJECT' < ui-deployment.yaml | kubectl apply --namespace=$project -f -
        kubectl apply -f ui-service.yaml --namespace=$project
    fi
}


rm -rf ~/.kube

# Connect to PSNC server and log in at Docker
if oc login $serverUrl --token=$token;
then
    compileApp

    if [[ $_project = 'all' ]]
    then 
        for project in 'ren-prototype' 'renergetic-wp4' 'renergetic-wp5' 'renergetic-wp6';
        do
            if resetConnection;
            then
                export PROJECT=$project
                oc project $project

                installPSNC
            fi
        done
    else
        project=$_project
        export PROJECT=$project

        if resetConnection;
        then
            oc project $project

            installPSNC
        fi
    fi
fi