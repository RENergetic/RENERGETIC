
token=$(grep -ioP "(?<=token=).+" _credentials.properties)

option=-1

if oc login https://console.paas-dev.psnc.pl --token=$token;
then
    oc project ren-prototype
    clear

read -r -d '' menu <<- EOM
What port want do you want forward?
- Front:
    1. Front Vue
    2. Grafana
- APIs:
    3. BackDB API
    4. BackBuildings
    5. BackInflux
- Databases:
    6. PostgreSQL
    7. InfluxDB
- Security:
    8. Keycloak
    9. WSO2 API Manager
0. Exit
EOM

    while [ $option != 0 ]
    do
        echo "$menu"
        echo 'Select an option: '
        read option
        case $option in
        1)
            start kubectl port-forward service/frontvue-sv 80:8080
        ;;
        2)
            start kubectl port-forward service/grafana-sv 3000:3000
        ;;
        3)
            start kubectl port-forward service/backdb-sv 8082:8082
        ;;
        4)
            start kubectl port-forward service/backbuilding-sv 8084:8082
        ;;
        5)
            start kubectl port-forward service/backinflux-sv 8086:8082
        ;;
        6)
            start kubectl port-forward service/postgresql-db-sv 5432:5432
        ;;
        7)
            start kubectl port-forward service/influx-sv 8086:8086
        ;;
        8)
            start kubectl port-forward service/keycloak-sv 8080:8080
        ;;
        9)
            start kubectl port-forward service/wso-sv 9443:9443
            start kubectl port-forward service/wso-sv 8280:8280
        ;;
        esac
        clear
    done

else
    echo "Can't connect with OpenShift server"
fi