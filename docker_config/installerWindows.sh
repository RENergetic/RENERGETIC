current=$(pwd -W tr / \\)

java='services\backdb'
javafile='backdb-0.0.1-SNAPSHOT.jar'
vue='front\renergetic'

installdb='true'
installapi='true'
installfront='true'

minikube start --driver=docker

if [[ $installdb = 'true' ]]
then
    cd  "${current}\..\db\docker"
    # DOCKERIZING THE DATABASE
    kubectl apply -f postgresql-configmap.yaml
    kubectl apply -f postgresql.yaml
    kubectl apply -f postgresql-service.yaml
	echo "Postgresql database successfully dockerized!"
fi

if [[ $installapi = 'true' ]]
then
    if [[ $java != '' ]]
    then
        # PACKAGING API
        cd "${current}\..\services\backdb"
        mvn clean package -Dmaven.test.skip
        cp ".\\target\\${javafile}" "${current}\\api"
    fi

    cd  "${current}\\api"
    # DOCKERIZING API SPRING
    eval $(minikube docker-env)
    docker build --no-cache --force-rm --tag=backdb:latest .
    kubectl apply -f backdb-deployment.yaml --force=true
    kubectl apply -f backdb-service.yaml
	echo "***********************************"
	echo "Spring API successfully dockerized!"
	echo "***********************************"
    clear
fi

if [[ $installfront = 'true' ]]
then
    if [[ $vue != '' ]]
    then
        # PREPARACION DEL JAR
        cd "${current}\..\front\renergetic"
        npm run build --prod
        rm -f -r "${current}\\front\\dist"
        cp -f -r ".\\dist" "${current}\\front\\dist"
    fi

    cd  "${current}\\front"
    # INSTALACION DE API SPRING
    eval $(minikube docker-env)
    docker build --no-cache --force-rm --tag=frontvue:latest .
    kubectl apply -f frontvue-deployment.yaml --force=true
    kubectl apply -f frontvue-service.yaml
	echo "*****************************************"
	echo "Vue.js component successfully dockerized!"
	echo "*****************************************"
    clear
fi
	echo "Installation has finished :). Remember to execute in a different console:"
	echo "	kubectl port-forward service/backdb-sv 8082:8082"
	echo "	minikube service frontvue-sv"
    read -p "Press any key to end ..."