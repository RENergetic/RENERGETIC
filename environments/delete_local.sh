
# Minikube namespace
project=$(grep -ioP "(project\s*=\s*)\K.+" _installers.properties)

# Delete PostgreSQL deployment | WARN: this DB have WSO2, APIs and Keycloak data
postgreSQL='true'

# Delete InfluxDB deployment
influx='true'

# Delete Grafana deployment
grafana='true'

# Removes the volumes of deleted deployments
unusedVolumes='true'

# Connect to Minikube
if ! minikube status &> /dev/null;
then 
    minikube start --driver=docker
fi
if minikube status;
then

    if [[ $postgreSQL = 'true' ]]
    then
        kubectl delete configmaps/postgresql-db-config --namespace=$project
        kubectl delete statefulsets/postgresql-db --namespace=$project
        kubectl delete services/postgresql-db-sv --namespace=$project
        kubectl delete pvc/postgresql-db-disk-postgresql-db-0 --namespace=$project --grace-period=0 --force
    fi

    if [[ $influx = 'true' ]]
    then
        kubectl delete deployments/influx-db --namespace=$project
        kubectl delete services/influx-db-sv --namespace=$project
        kubectl delete secrets/influxdb-secrets --namespace=$project
        kubectl delete pvc/influx-disk --namespace=$project --grace-period=0 --force
        kubectl delete pvc/influx2-disk --namespace=$project --grace-period=0 --force
    fi

    if [[ $grafana = 'true' ]]
    then
        kubectl delete configmaps/grafana-ini --namespace=$project
        kubectl delete deployments/grafana --namespace=$project
        kubectl delete services/grafana-sv --namespace=$project
        kubectl delete pvc/grafana-disk --namespace=$project --grace-period=0 --force
    fi

    if [[ $unusedVolumes = 'true' ]]
    then
        kubectl get pv | grep Released | awk '$1 {print$1}' | while read vol; do kubectl delete pv/${vol} --grace-period=0 --force; done
    fi

fi

