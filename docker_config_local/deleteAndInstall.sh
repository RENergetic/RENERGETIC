basedir=$(pwd -W tr / \\)

javahdr='services\hdrapi'
javafilehdr='hdrapi-0.0.1-SNAPSHOT.jar'
vue='front\renergetic_ui'
installdb='true'
installapihdr='true'  # API HDR
installfront='true'
installkeycloak='true'

#influxdb
installdb='true'
#influxdb java api
installapi='true'
#nifi
installback='false'
installkafka='false'
installgrafana='true'
javafile='measurementapi-0.0.1-SNAPSHOT.jar'

minikube start --driver=docker

kubectl delete namespace app
kubectl delete namespace data
kubectl get pv | grep Released | awk '$1 {print$1}' | while read vol; do kubectl delete pv/${vol}; done

cd "${basedir}\\app"
current=$(pwd -W tr / \\)

namespace='app'

# DELETE VOLUMES

kubectl create namespace $namespace

if [[ $installdb = 'true' ]]
then
    cd  "${current}\\db"
    # DATABASE INSTALATION
	# delete kubernetes resources if exists
    kubectl delete configmaps/postgresql-db-config --namespace=$namespace
    kubectl delete statefulsets/postgresql-db --namespace=$namespace
    kubectl delete services/postgresql-db-sv --namespace=$namespace
	
    # create kubernetes resources
    kubectl apply -f postgresql-configmap.yaml --namespace=$namespace
    kubectl apply -f postgresql.yaml --namespace=$namespace
    kubectl apply -f postgresql-service.yaml --namespace=$namespace
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

cd "${basedir}\\data"
current=$(pwd -W tr / \\)

namespace='data'

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
    cd  "${current}\db"
    # DATABASE INSTALATION
    # delete kubernetes resources if exists
    kubectl delete deployment/influx-db --namespace=$namespace
    kubectl delete services/influx-db-sv --namespace=$namespace

    # create kubernetes resources
    kubectl apply -f influx-volume.yaml --namespace=$namespace
    kubectl apply -f influx-deployment.yaml --namespace=$namespace
    kubectl apply -f influx-service.yaml --namespace=$namespace
fi

if [[ $installapi = 'true' ]]
then
    # API COMPILE TO JAR
    cd "${current}\\..\\..\\services\\measurementapi"
    mvn clean package -Dmaven.test.skip
    cp ".\\target\\${javafile}" "${current}\\api"

    cd  "${current}\\api"
    # API INSTALLATION
    # set environment variables
    eval $(minikube docker-env)

    # delete kubernetes resources if exists
    kubectl delete deployments/backinflux --namespace=$namespace
    kubectl delete services/backinflux-sv --namespace=$namespace

    # create docker image
    docker build --no-cache --force-rm --tag=backinflux:latest .
    
    # create kubernetes resources
    kubectl apply -f backinflux-deployment.yaml --force=true --namespace=$namespace
    kubectl apply -f backinflux-service.yaml --namespace=$namespace
fi

if [[ $installback = 'true' ]]
then
    cd  "${current}\\back"
    # NIFI INSTALLATION
    # set environment variables
    eval $(minikube docker-env)

    # delete kubernetes resources if exists
    kubectl delete deployments/nifi --namespace=$namespace
    kubectl delete services/nifi-sv --namespace=$namespace

    # create docker image
    docker build --no-cache --force-rm --tag=nifi:latest .
    
    # create kubernetes resources
    #-kubectl apply -f nifi-deployment.yaml --force=true --namespace=$namespace
    #-kubectl apply -f nifi-service.yaml --namespace=$namespace
    kubectl apply -f nifi.yaml --force=true --namespace=$namespace
fi

if [[ $installkafka = 'true' ]]
then
    cd  "${current}\\kafka"
    # KAFKA INSTALATION
    # delete kubernetes resources if exists
    kubectl delete deployments/zookeeper --namespace=$namespace
    kubectl delete services/zookeeper-sv --namespace=$namespace

    kubectl delete deployments/kafka-broker --namespace=$namespace
    kubectl delete services/kafka-sv --namespace=$namespace

    # create kubernetes resources
    kubectl apply -f zookeeper.yaml --namespace=$namespace
    kubectl apply -f kafka.yaml --namespace=$namespace
fi

if [[ $installgrafana = 'true' ]]
then
    cd  "${current}\\grafana"
    # GRAFANA INSTALATION
    # delete kubernetes resources if exists
    kubectl delete deployments/grafana --namespace=$namespace
    kubectl delete services/grafana-sv --namespace=$namespace

    # create kubernetes resources
    kubectl apply -f grafana-config.yaml --namespace=$namespace
    kubectl apply -f grafana-volume.yaml --namespace=$namespace
    kubectl apply -f grafana-deployment.yaml --namespace=$namespace
    kubectl apply -f grafana-service.yaml --namespace=$namespace
fi

echo "Installation has finished :). Remember to execute in a different console:"
	echo "	minikube service grafana-sv --namespace ${namespace}"