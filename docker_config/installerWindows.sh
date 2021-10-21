current=$(pwd -W tr / \\)
java='services\backdb'
javafile='backdb-0.0.1-SNAPSHOT.jar'

installdb='false'
installapi='true'

minikube start --driver=docker

if [[ $installdb = 'true' ]]
then
    cd  "${current}\..\db\docker"
    # INSTALACION DE BASE DE DATOS
    kubectl apply -f postgresql-configmap.yaml
    kubectl apply -f postgresql.yaml
    kubectl apply -f postgresql-service.yaml
fi

if [[ $installapi = 'true' ]]
then
    if [[ $java != '' ]]
    then
        # PREPARACION DEL JAR
        cd "${current}\..\services\backdb"
        mvn clean package -Dmaven.test.skip
        cp ".\\target\\${javafile}" ".\\src\main\docker"
    fi

    cd  "${current}\..\services\backdb\src\main\docker"
    # INSTALACION DE API SPRING
    eval $(minikube docker-env)
    docker build --no-cache --force-rm --tag=backdb:latest .
    kubectl apply -f backdb-deployment.yaml --force=true
    kubectl apply -f backdb-service.yaml
    read -p "La instalacion ha finalizado pulsa una tecla para salir ..."
    clear
fi