
token=$(grep -ioP "(?<=token=).+" _credentials.properties)

if oc login https://console.paas-dev.psnc.pl --token=$token;
then
    oc project ren-prototype-devops
    start kubectl port-forward service/sonarqube-sv 9000:9000
    echo "Service forwarded sucessfully"
else
    echo "Can't connect with OpenShift server"
fi