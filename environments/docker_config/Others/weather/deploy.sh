
serverUrl=$(grep -ioP "(server\s*=\s*)\K.+" ../../../_credentials.properties)

# Project name
project="ren-prototype-devops"

# Credentials
user=$(grep -ioP "(user\s*=\s*)\K.+" ../../../_credentials.properties)
token=$(grep -ioP "(token\s*=\s*)\K.+" ../../../_credentials.properties)

rm -rf ~/.kube

# Connect to PSNC server and log in at Docker
if oc login $serverUrl --token=$token;
then
    oc project $project

    # set environment variables
    eval $(minikube docker-env)

    # delete kubernetes resources if exists
    kubectl delete deployments/weather-info

    docker build --no-cache --force-rm --tag=registry.apps.paas-dev.psnc.pl/$project/weather-info:latest .
    docker login -u $user -p $token https://registry.apps.paas-dev.psnc.pl/
    docker push registry.apps.paas-dev.psnc.pl/$project/weather-info:latest

    # create kubernetes resources
    kubectl apply --force=true -f weather-deployment.yaml
fi
