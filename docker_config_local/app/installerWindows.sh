current=$(pwd -W tr / \\)

namespace='app'

java='services\backdb'
javafile='backdb-0.0.1-SNAPSHOT.jar'
javahdr='services\hdrapi'
javafilehdr='hdrapi-0.0.1-SNAPSHOT.jar'
vue='front\renergetic_ui'

installdb='true'
installapi='true'
installapihdr='true'  # API HDR
installfront='true'
installkeycloak='true'

java1='services\backbuildings'
javafile1='buildingsService-0.0.1-SNAPSHOT.jar'
installapi1='true'

while getopts n: flag
do
    case "${flag}" in
        n) namespace=${OPTARG};
    esac
done

minikube start --driver=docker
kubectl create namespace $namespace

if [[ $installdb = 'true' ]]
then
    cd  "${current}\\db"
    # DATABASE INSTALATION
	# delete kubernetes resources if exists
    kubectl delete configmaps/postgres-db-config --namespace=$namespace
    kubectl delete statefulsets/postgresql-db --namespace=$namespace
    kubectl delete services/postgres-db-sv --namespace=$namespace
	
    # create kubernetes resources
    kubectl apply -f postgresql-configmap.yaml --namespace=$namespace
    kubectl apply -f postgresql.yaml --namespace=$namespace
    kubectl apply -f postgresql-service.yaml --namespace=$namespace
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
    kubectl delete deployments/backdb --namespace=$namespace
    kubectl delete services/backdb-sv --namespace=$namespace

    # create docker image
    docker build --no-cache --force-rm --tag=backdb:latest .
    
    # create kubernetes resources
    kubectl apply -f backdb-deployment.yaml --force=true --namespace=$namespace
    kubectl apply -f backdb-service.yaml --namespace=$namespace
fi

if [[ $installapihdr = 'true' ]]
then
    if [[ $javahdr != '' ]]
    then
        # API COMPILE TO JAR
        cd "${current}\\..\\..\\services\\hdrapi"
        mvn clean package -Dmaven.test.skip
        cp ".\\target\\${javafilehdr}" "${current}\\hdr-api"
    fi

    cd  "${current}\\hdr-api"
    # API INSTALLATION
    # set environment variables
    eval $(minikube docker-env)

    # delete kubernetes resources if exists
    kubectl delete deployments/hdr-api --namespace=$namespace
    kubectl delete services/hdr-api-sv --namespace=$namespace

    # create docker image
    docker build --no-cache --force-rm --tag=hdr:latest .
    
    # create kubernetes resources
    kubectl apply -f hdr-deployment.yaml --force=true --namespace=$namespace
    kubectl apply -f hdr-service.yaml --namespace=$namespace
fi

if [[ $installkeycloak = 'true' ]]
then
    if [[ $vue != '' ]]
    then
        # COMPILE KEYCLOAK FILES TO PRODUCTION
        cd "${current}\\..\\..\\keycloak\\themes"
        rm -f -r "${current}\\keycloak\\themes\\renergetic"
        mkdir -p "${current}\\keycloak\\themes"
        cp -f -r ".\\renergetic" "${current}\\keycloak\\themes\\renergetic"
    fi

    cd  "${current}\\keycloak"
    # FRONTEND INSTALLATION
    # set environment variables
    eval $(minikube docker-env)

    # delete kubernetes resources if exists
    kubectl delete deployments/keycloak --namespace=$namespace
    kubectl delete services/keycloak-sv --namespace=$namespace

    # create docker image
    docker build --no-cache --force-rm --tag=keycloak:latest .

    # create kubernetes resources
    kubectl apply -f keycloak-volume.yaml --namespace=$namespace
    kubectl apply -f keycloak-deployment.yaml --force=true --namespace=$namespace
    kubectl apply -f keycloak-service.yaml --namespace=$namespace
fi

if [[ $installfront = 'true' ]]
then
    if [[ $vue != '' ]]
    then
        # COMPILE VUE FILES TO PRODUCTION
        cd "${current}\\..\\..\\front\\renergetic_ui"
        cp -f "${current}\\front\\.env" ".env"
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
    kubectl delete deployments/frontvue --namespace=$namespace
    kubectl delete services/frontvue-sv --namespace=$namespace

    # create docker image
    docker build --no-cache --force-rm --tag=frontvue:latest .

    # create kubernetes resources
    kubectl apply -f frontvue-deployment.yaml --force=true --namespace=$namespace
    kubectl apply -f frontvue-service.yaml --namespace=$namespace
fi

echo "Installation has finished :). Remember to execute in a different console:"
	echo "	minikube service frontvue-sv --namespace ${namespace}"
#    read -p "Press any key to end ..."
clear