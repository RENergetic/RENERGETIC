current=$(pwd -W tr / \\)

user=$(grep -ioP "(?<=user=).+" ../_credentials.properties)
token=$(grep -ioP "(?<=token=).+" ../_credentials.properties)
project='ren-prototype'

buildimages='true'
compileapps='true'
automatic=-1

installdb='true'
installapi='true'
installback=''
installkafka=''
installgrafana='true'
javafile='backinflux-0.0.1-SNAPSHOT.jar'

while getopts "ab:c:" flag
do
    case "${flag}" in
        a) automatic=${OPTARG};;
        b) buildimages=${OPTARG};;
        c) compileapps=${OPTARG};;
    esac
done
echo $buildimages
if oc login https://console.paas-dev.psnc.pl --token=$token;
then
oc project $project

if [[ $installdb = 'true' ]]
then
    cd  "${current}\db"
    # DATABASE INSTALATION
    # delete kubernetes resources if exists
    kubectl delete deployment/influx-db
    kubectl delete services/influx-db-sv
    
    if [[ $buildimages = 'true' ]]
    then
        # create docker image
        docker build --no-cache --force-rm --tag=registry.apps.paas-dev.psnc.pl/$project/influxdb:latest .
        docker login -u $user -p $token https://registry.apps.paas-dev.psnc.pl/
        docker push registry.apps.paas-dev.psnc.pl/$project/influxdb:latest
    fi

    # create kubernetes resources
    kubectl apply -f influx-secrets.yaml
    kubectl apply -f influx-volume.yaml
    kubectl apply -f influx-deployment.yaml
    kubectl apply -f influx-service.yaml
fi

if [[ $installapi = 'true' ]]
then
    if [[ $compileapps = 'true' ]]
    then
        # API COMPILE TO JAR
        cd "${current}\\..\\..\\services\\backinflux"
        mvn clean package -Dmaven.test.skip
        cp ".\\target\\${javafile}" "${current}\\api"
    fi

    cd  "${current}\\api"
    # API INSTALLATION
    # set environment variables
    eval $(minikube docker-env)

    # delete kubernetes resources if exists
    kubectl delete deployments/backinflux
    kubectl delete services/backinflux-sv

    
    if [[ $buildimages = 'true' ]]
    then
        # create docker image
        docker build --no-cache --force-rm --tag=registry.apps.paas-dev.psnc.pl/$project/backinflux:latest .
        docker login -u $user -p $token https://registry.apps.paas-dev.psnc.pl/
        docker push registry.apps.paas-dev.psnc.pl/$project/backinflux:latest
    fi
    
    # create kubernetes resources
    kubectl apply -f backinflux-deployment.yaml --force=true
    kubectl apply -f backinflux-service.yaml
fi

if [[ $installback = 'true' ]]
then
    cd  "${current}\\back"
    # NIFI INSTALLATION
    # set environment variables
    eval $(minikube docker-env)

    # delete kubernetes resources if exists
    kubectl delete deployments/nifi
    kubectl delete services/nifi-sv

    # create docker image
    docker build --no-cache --force-rm --tag=nifi:latest .
    
    # create kubernetes resources
    #-kubectl apply -f nifi-deployment.yaml --force=true --namespace=$namespace
    #-kubectl apply -f nifi-service.yaml --namespace=$namespace
    kubectl apply -f nifi.yaml --force=true
fi

if [[ $installkafka = 'true' ]]
then
    cd  "${current}\\kafka"
    # KAFKA INSTALATION
    # delete kubernetes resources if exists
    kubectl delete deployments/zookeeper
    kubectl delete services/zookeeper-sv

    kubectl delete deployments/kafka-broker
    kubectl delete services/kafka-sv

    # create kubernetes resources
    kubectl apply -f zookeeper.yaml
    kubectl apply -f kafka.yaml
fi

if [[ $installgrafana = 'true' ]]
then
    cd  "${current}\\grafana"
    # GRAFANA INSTALATION
    # delete kubernetes resources if exists
    kubectl delete deployments/grafana
    kubectl delete services/grafana-sv


    if [[ $buildimages = 'true' ]]
    then
        # create docker image
        docker build --no-cache --force-rm --tag=registry.apps.paas-dev.psnc.pl/$project/grafana:latest .
        docker login -u $user -p $token https://registry.apps.paas-dev.psnc.pl/
        docker push registry.apps.paas-dev.psnc.pl/$project/grafana:latest
    fi

    # create kubernetes resources
    kubectl apply -f grafana-config.yaml
    kubectl apply -f grafana-volume.yaml
    kubectl apply -f grafana-deployment.yaml
    kubectl apply -f grafana-service.yaml
fi

    echo "Installation has finished :). Remember to execute in a different console:"
        echo "	minikube service grafana-sv"

    if [ $automatic != '-1' ]
    then
        read -p "Press any key to end ..."
        clear
    fi
else
    echo "Can't connect with OpenShift server"
    read -p "Press any key to end ..."
    clear
fi