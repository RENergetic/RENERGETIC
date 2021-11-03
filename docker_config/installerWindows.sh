current=$(pwd -W tr / \\)

java='services\backdb'
javafile='backdb-0.0.1-SNAPSHOT.jar'
vue='front\renergetic'

installdb='false'
installapi='true'
installfront='true'

minikube start --driver=docker

if [[ $installdb = 'true' ]]
then
    cd  "${current}\db"
    # DATABASE INSTALATION
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
        cd "${current}\..\services\backdb"
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
    docker build --no-cache --force-rm --tag=backdb:latest .
    
    # create kubernetes resources
    kubectl apply -f backdb-deployment.yaml --force=true
    kubectl apply -f backdb-service.yaml
fi

if [[ $installfront = 'true' ]]
then
    if [[ $vue != '' ]]
    then
        # COMPILE VUE FILES TO PRODUCTION
        cd "${current}\..\front\renergetic"
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

    # create docker image
    docker build --no-cache --force-rm --tag=frontvue:latest .

    # create kubernetes resources
    kubectl apply -f frontvue-deployment.yaml --force=true
    kubectl apply -f frontvue-service.yaml
fi

echo "Installation has finished :). Remember to execute in a different console:"
	echo "	minikube service frontvue-sv"
    read -p "Press any key to end ..."
clear