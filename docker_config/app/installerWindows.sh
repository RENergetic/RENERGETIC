current=$(pwd -W tr / \\)

user='your_user'
token='your_token'
project='ren-prototype'

java='services\backdb'
javafile='backdb-0.0.1-SNAPSHOT.jar'
vue='front\renergetic'

installdb='true'
installapi='true'
installfront='true'
installkeycloak='true'
installwso='true'

java1='services\backbuildings'
javafile1='buildingsService-0.0.1-SNAPSHOT.jar'
installapi1='true'

oc login https://console.paas-dev.psnc.pl --token=$token
oc project $project

if [[ $installdb = 'true' ]]
then
    cd  "${current}\\db"
    # DATABASE INSTALATION
	# delete kubernetes resources if exists
    kubectl delete configmaps/postgresql-db-config
    kubectl delete statefulsets/postgresql-db
    kubectl delete services/postgresql-db-sv
	
    # create docker image
    docker build --no-cache --force-rm --tag=registry.apps.paas-dev.psnc.pl/$project/postgres:latest .
    docker login -u $user -p $token https://registry.apps.paas-dev.psnc.pl/
    docker push registry.apps.paas-dev.psnc.pl/$project/postgres:latest

    # create kubernetes resources
    kubectl apply -f postgresql-configmap.yaml
    kubectl apply -f postgresql.yaml
    kubectl apply -f postgresql-service.yaml
fi

if [[ $installapi = 'true' ]]
then
    if [[ $java != '' ]]
    then
        # API COMPILE TO JAR
        cd "${current}\\..\\..\\services\\backdb"
        mvn clean package -Dmaven.test.skip
        cp ".\\target\\${javafile}" "${current}\\api"
    fi

    cd  "${current}\\api"
    # API INSTALLATION
    # set environment variables
    eval $(minikube docker-env)

    # delete kubernetes resources if exists
    kubectl delete deployments/backdb
    kubectl delete services/backdb-sv

    # create docker image
    docker build --no-cache --force-rm --tag=registry.apps.paas-dev.psnc.pl/$project/backdb:latest .
    docker login -u $user -p $token https://registry.apps.paas-dev.psnc.pl/
    docker push registry.apps.paas-dev.psnc.pl/$project/backdb:latest
    
    # create kubernetes resources
    kubectl apply -f backdb-deployment.yaml --force=true
    kubectl apply -f backdb-service.yaml
fi

if [[ $installapi1 = 'true' ]]
then
    if [[ $java1 != '' ]]
    then
        # API COMPILE TO JAR
        cd "${current}\\..\\..\\services\\buildingsService"
        mvn clean package -Dmaven.test.skip
        cp ".\\target\\${javafile1}" "${current}\\buildings"
    fi

    cd  "${current}\\buildings"
    # API INSTALLATION
    # set environment variables
    eval $(minikube docker-env)

    # delete kubernetes resources if exists
    kubectl delete deployments/backbuildings
    kubectl delete services/backbuildings-sv

    # create docker image
    docker build --no-cache --force-rm --tag=registry.apps.paas-dev.psnc.pl/$project/backbuildings:latest .
    docker login -u $user -p $token https://registry.apps.paas-dev.psnc.pl/
    docker push registry.apps.paas-dev.psnc.pl/$project/backbuildings:latest
    
    # create kubernetes resources
    kubectl apply -f backbuildings-deployment.yaml --force=true
    kubectl apply -f backbuildings-service.yaml
fi

if [[ $installkeycloak = 'true' ]]
then
    # COMPILE KEYCLOAK FILES TO PRODUCTION
    cd "${current}\\..\\..\\keycloak\\themes"
    rm -f -r "${current}\\keycloak\\themes\\renergetic"
    mkdir -p "${current}\\keycloak\\themes"
    cp -f -r ".\\renergetic" "${current}\\keycloak\\themes\\renergetic"

    cd  "${current}\\keycloak"
    # FRONTEND INSTALLATION
    # set environment variables
    eval $(minikube docker-env)

    # delete kubernetes resources if exists
    kubectl delete deployments/keycloak
    kubectl delete services/keycloak-sv

    # create docker image
    docker build --no-cache --force-rm --tag=registry.apps.paas-dev.psnc.pl/$project/keycloak:latest .
    docker login -u $user -p $token https://registry.apps.paas-dev.psnc.pl/
    docker push registry.apps.paas-dev.psnc.pl/$project/keycloak:latest

    # create kubernetes resources
    kubectl apply -f keycloak-deployment.yaml --force=true
    kubectl apply -f keycloak-service.yaml
fi

if [[ $installwso = 'true' ]]
then
    cd  "${current}\\wso2"
    # WSO2 (API MANAGER) INSTALLATION
    # set environment variables
    eval $(minikube docker-env)

    # create docker image
    docker build --no-cache --force-rm --tag=registry.apps.paas-dev.psnc.pl/$project/wso2:latest .
    docker login -u $user -p $token https://registry.apps.paas-dev.psnc.pl/
    docker push registry.apps.paas-dev.psnc.pl/$project/wso2:latest

    # delete kubernetes resources if exists
    kubectl delete deployments/wso --namespace=$namespace
    kubectl delete services/wso-sv --namespace=$namespace

    # create kubernetes resources
    kubectl apply -f wso2-volume.yaml --namespace=$namespace
    kubectl apply -f wso2-deployment.yaml --force=true --namespace=$namespace
    kubectl apply -f wso2-service.yaml --namespace=$namespace
fi

if [[ $installfront = 'true' ]]
then
    if [[ $vue != '' ]]
    then
        # COMPILE VUE FILES TO PRODUCTION
        cd "${current}\\..\\..\\front\\renergetic"
        npm install
        npm run build --prod
        rm -f -r "${current}\\front\\dist"
        cp -f -r ".\\dist" "${current}\\front\\dist"
    fi

    cd  "${current}\\front"
    # FRONTEND INSTALLATION
    # set environment variables
    eval $(minikube docker-env)

    # delete kubernetes resources if exists
    kubectl delete deployments/frontvue
    kubectl delete services/frontvue-sv
    kubectl delete configmaps/frontvue-configmap-ui
    kubectl delete configmaps/frontvue-configmap-config

    # create docker image
    docker build --no-cache --force-rm --tag=registry.apps.paas-dev.psnc.pl/$project/frontvue:latest .
    docker login -u $user -p $token https://registry.apps.paas-dev.psnc.pl/
    docker push registry.apps.paas-dev.psnc.pl/$project/frontvue:latest

    # create configuration files
    #kubectl create configmap frontvue-configmap-ui --from-file=./dist/
    #kubectl create configmap frontvue-configmap-config --from-file=./default.conf

    # create kubernetes resources
    kubectl apply -f frontvue-deployment.yaml --force=true
    kubectl apply -f frontvue-service.yaml
fi

echo "Installation has finished :). Remember to execute in a different console:"
	echo "	minikube service frontvue-sv --namespace ${namespace}"
    read -p "Press any key to end ..."
clear