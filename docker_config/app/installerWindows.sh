current=$(pwd -W tr / \\)

user='PaaS_user'
token='Personal_token'
project='ren-prototype'

buildimages='true'
compileapps='true'

javafile='backdb-0.0.1-SNAPSHOT.jar'
javafile1='buildingsService-0.0.1-SNAPSHOT.jar'

installdb='true'
installapi='true'   # API ISLANDS
installapi1='true'  # API BUILDINGS
installfront='true'
installkeycloak='true'
installwso='true'

oc login https://console.paas-dev.psnc.pl --token=$token
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

if [[ $installapi = 'true' ]]
then
    if [[ $compileapps = 'true' ]]
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
    kubectl delete deployments/backdb
    kubectl delete services/backdb-sv


    if [[ $buildimages = 'true' ]]
    then
        # create docker image
        docker build --no-cache --force-rm --tag=registry.apps.paas-dev.psnc.pl/$project/backdb:latest .
        docker login -u $user -p $token https://registry.apps.paas-dev.psnc.pl/
        docker push registry.apps.paas-dev.psnc.pl/$project/backdb:latest
    fi
    
    # create kubernetes resources
    kubectl apply -f backdb-deployment.yaml --force=true
    kubectl apply -f backdb-service.yaml
fi

if [[ $installapi1 = 'true' ]]
then
    if [[ $compileapps = 'true' ]]
    then
        # API COMPILE TO JAR
        cd "${current}\\..\\..\\services\\buildingsService"
        mvn clean package -Dmaven.test.skip
        cp ".\\target\\${javafile1}" "${current}\\buildings"
    fi

    cd  "${current}\\buildings"
    # API INSTALLATION
    # set environment variables
    eval $(minikube docker-env)

    # delete kubernetes resources if exists
    kubectl delete deployments/backbuildings
    kubectl delete services/backbuildings-sv


    if [[ $buildimages = 'true' ]]
    then
        # create docker image
        docker build --no-cache --force-rm --tag=registry.apps.paas-dev.psnc.pl/$project/backbuildings:latest .
        docker login -u $user -p $token https://registry.apps.paas-dev.psnc.pl/
        docker push registry.apps.paas-dev.psnc.pl/$project/backbuildings:latest
    fi
    
    # create kubernetes resources
    kubectl apply -f backbuildings-deployment.yaml --force=true
    kubectl apply -f backbuildings-service.yaml
fi

if [[ $installkeycloak = 'true' ]]
then
    if [[ $compileapps = 'true' ]]
    then
        # COMPILE KEYCLOAK FILES TO PRODUCTION
        cd "${current}\\..\\..\\keycloak\\themes"
        rm -f -r "${current}\\keycloak\\themes\\renergetic"
        mkdir -p "${current}\\keycloak\\themes"
        cp -f -r ".\\renergetic" "${current}\\keycloak\\themes\\renergetic"
    fi
    cd  "${current}\\keycloak"
    # KEYCLOAK INSTALLATION
    # set environment variables
    eval $(minikube docker-env)

    # delete kubernetes resources if exists
    kubectl delete deployments/keycloak
    kubectl delete services/keycloak-sv


    if [[ $buildimages = 'true' ]]
    then
        # create docker image
        docker build --no-cache --force-rm --tag=registry.apps.paas-dev.psnc.pl/$project/keycloak:latest .
        docker login -u $user -p $token https://registry.apps.paas-dev.psnc.pl/
        docker push registry.apps.paas-dev.psnc.pl/$project/keycloak:latest
    fi

    # create kubernetes resources
    kubectl apply -f keycloak-deployment.yaml --force=true
    kubectl apply -f keycloak-service.yaml
fi

if [[ $installwso = 'true' ]]
then
    cd  "${current}\\wso2"
    # WSO2 (API MANAGER) INSTALLATION
    # set environment variables
    eval $(minikube docker-env)

    # delete kubernetes resources if exists
    kubectl delete deployments/wso
    kubectl delete services/wso-sv


    if [[ $buildimages = 'true' ]]
    then
        # create docker image
        docker build --no-cache --force-rm --tag=registry.apps.paas-dev.psnc.pl/$project/wso2:latest .
        docker login -u $user -p $token https://registry.apps.paas-dev.psnc.pl/
        docker push registry.apps.paas-dev.psnc.pl/$project/wso2:latest
    fi

    # create kubernetes resources
#    kubectl apply -f wso2-volume.yaml
    kubectl apply -f wso2-deployment.yaml --force=true
    kubectl apply -f wso2-service.yaml
fi

if [[ $installfront = 'true' ]]
then
    if [[ $compileapps = 'true' ]]
    then
        # COMPILE VUE FILES TO PRODUCTION
        cd "${current}\\..\\..\\front\\renergetic"
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
    kubectl delete deployments/frontvue
    kubectl delete services/frontvue-sv

    if [[ $buildimages = 'true' ]]
    then
        # create docker image
        docker build --no-cache --force-rm --tag=registry.apps.paas-dev.psnc.pl/$project/frontvue:latest .
        docker login -u $user -p $token https://registry.apps.paas-dev.psnc.pl/
        docker push registry.apps.paas-dev.psnc.pl/$project/frontvue:latest
    fi

    # create kubernetes resources
    kubectl apply -f frontvue-deployment.yaml --force=true
    kubectl apply -f frontvue-service.yaml
fi

echo "Installation has finished :). Remember to execute in a different console:"
	echo "	minikube service frontvue-sv --namespace ${namespace}"
    read -p "Press any key to end ..."
clear