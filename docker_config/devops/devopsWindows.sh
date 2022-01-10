current=$(pwd -W tr / \\)

user='your_user'
token='your_token'
project='ren-prototype-devops'

# CREATE A NAMESPACE IN KUBERNETE TO TEST JENKINS AND SONARKUBE
# CREATE A KUBERNETES INSTANCE OF PROGRAMS IF VARIABLES ARE SETTING TO TRUE
installsonarqube='true'
installjenkins=''

oc login https://console.paas-dev.psnc.pl --token=$token
oc project $project

if [[ $installsonarqube = 'true' ]]
then
    cd  "${current}\\sonarqube"
    # SONARQUBE
    # set environment variables
    eval $(minikube docker-env)

    # delete kubernetes resources if exists
    kubectl delete deployments/sonarqube
    kubectl delete services/sonarqube-sv

    # create docker image
    docker build --no-cache --force-rm --tag=registry.apps.paas-dev.psnc.pl/$project/sonarqube:latest .
    docker login -u $user -p $token https://registry.apps.paas-dev.psnc.pl/
    docker push registry.apps.paas-dev.psnc.pl/$project/sonarqube:latest

    # create kubernetes resources
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

read -p "La instalacion ha finalizado pulsa una tecla para salir ..."
clear