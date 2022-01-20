current=$(pwd -W tr / \\)

user=$(grep -ioP "(?<=user=).+" ../_credentials.properties)
token=$(grep -ioP "(?<=token=).+" ../_credentials.properties)
project='ren-prototype-devops'

buildimages='true'
automatic=-1

# CREATE A NAMESPACE IN KUBERNETES TO TEST JENKINS AND SONARKUBE
# CREATE A KUBERNETES INSTANCE OF PROGRAMS IF VARIABLES ARE SETTING TO TRUE
installdb='true'
installsonarqube='true'
installjenkins=''

while getopts "ab:" flag
do
    case "${flag}" in
        a) automatic=${OPTARG};;
        b) buildimages=${OPTARG};;
    esac
done

if oc login https://console.paas-dev.psnc.pl --token=$token;
then
oc project $project

if [[ $installdb = 'true' ]]
then
    cd  "${current}\\db"
    # DATABASE INSTALATION
	# delete kubernetes resources if exists
    kubectl delete configmaps/postgresql-db-config
    kubectl delete statefulsets/postgresql-db
    kubectl delete services/postgresql-db-sv
	
    if [[ $buildimages = 'true' ]]
    then
        # create docker image
        docker build --no-cache --force-rm --tag=registry.apps.paas-dev.psnc.pl/$project/postgres:latest .
        docker login -u $user -p $token https://registry.apps.paas-dev.psnc.pl/
        docker push registry.apps.paas-dev.psnc.pl/$project/postgres:latest
    fi

    # create kubernetes resources
    kubectl apply -f postgresql-configmap.yaml
    kubectl apply -f postgresql.yaml
    kubectl apply -f postgresql-service.yaml
fi

if [[ $installsonarqube = 'true' ]]
then
    cd  "${current}\\sonarqube"
    # SONARQUBE
    # set environment variables
    eval $(minikube docker-env)

    # delete kubernetes resources if exists
    kubectl delete deployments/sonarqube
    kubectl delete services/sonarqube-sv

    if [[ $buildimages = 'true' ]]
    then
        # create docker image
        docker build --no-cache --force-rm --tag=registry.apps.paas-dev.psnc.pl/$project/sonarqube:latest .
        docker login -u $user -p $token https://registry.apps.paas-dev.psnc.pl/
        docker push registry.apps.paas-dev.psnc.pl/$project/sonarqube:latest
    fi

    # create kubernetes resources
    kubectl apply -f sonar-disks.yaml
    kubectl apply -f sonar-deployment.yaml --force=true
    kubectl apply -f sonar-service.yaml
fi

if [[ $installjenkins = 'true' ]]
then
    cd  "${current}\\jenkins"
    # JENKINS INSTALLATION
    # set environment variables
    eval $(minikube docker-env)

    # delete kubernetes resources if exists
    kubectl delete deployments/jenkins --namespace=jenkins
    kubectl delete services/jenkins-sv --namespace=jenkins
    kubectl delete services/jenkins-sv-jnlp --namespace=jenkins

    # create docker image
    docker build --no-cache --force-rm --tag=jenkins:latest .

    # create kubernetes resources
    kubectl apply -f jenkins-deployment.yaml --force=true --namespace=jenkins
    kubectl apply -f jenkins-service.yaml --namespace=jenkins
fi
    if [ $automatic != '-1' ]
    then
        read -p "Installation has finished, Press any key to end ..."
        clear
    fi
else
    echo "Can't connect with OpenShift server"
    read -p "Press any key to end ..."
    clear
fi