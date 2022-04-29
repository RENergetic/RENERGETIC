current=$(pwd -W tr / \\)

namespace='data'

installdb='true'
installapi='true'
installback='false'
installkafka='false'
installgrafana='true'
javafile='measurementapi-0.0.1-SNAPSHOT.jar'

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
#    read -p "Press any key to end ..."
clear