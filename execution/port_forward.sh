
namespace="ren-prototype"

portRE='^[0-9]+$'
option=-1
port=0

# Connect to Minikube
if ! minikube status &> /dev/null;
then 
    minikube start --driver=docker
fi

if minikube status;
then

read -r -d '' menu <<- EOM
What port want do you want forward?
- Front:
    1. Renergetic UI
    2. Grafana
- APIs:
    3. HDR API
    4. Influx API
    5. Ingestion API
- Databases:
    6. PostgreSQL
    7. InfluxDB
- Security:
    8. Keycloak
    9. WSO2 API Manager
    10. WSO2 APIs
0. Exit
EOM

clear

    while [ $option != 0 ]
    do

        echo "$menu"
        echo 'Select an option: '
        read option

        if [ $option != 0 ];
        then
            echo 'Select a port: '
            read port

            if ! [[ $port =~ $portRE ]] ; 
            then
                clear
                echo "The port should be a number"
            elif [ "$port" -ge 80 ] && [ "$port" -le 9500 ];
            then
                clear
                case $option in
                1)
                    start kubectl port-forward service/renergetic-ui-sv $port:8080 --namespace=$namespace
                ;;
                2)
                    start kubectl port-forward service/grafana-sv $port:3000 --namespace=$namespace
                ;;
                3)
                    start kubectl port-forward service/hdr-api-sv $port:8082 --namespace=$namespace
                ;;
                4)
                    start kubectl port-forward service/influx-api-sv $port:8082 --namespace=$namespace
                ;;
                5)
                    start kubectl port-forward service/ingestion-api-sv $port:8082 --namespace=$namespace
                ;;
                6)
                    start kubectl port-forward service/postgresql-db-sv $port:5432 --namespace=$namespace
                ;;
                7)
                    start kubectl port-forward service/influx-db-sv $port:8086 --namespace=$namespace
                ;;
                8)
                    start kubectl port-forward service/keycloak-sv $port:8080 --namespace=$namespace
                ;;
                9)
                    start kubectl port-forward service/wso-sv $port:9443 --namespace=$namespace
                ;;
                10)
                    start kubectl port-forward service/wso-sv $port:8280 --namespace=$namespace
                ;;
                esac
                clear
            else
                clear
                echo "The port should be between 2000 and 9500"
            fi
        fi
    done

else
    echo "Can't connect with Minikube"
fi