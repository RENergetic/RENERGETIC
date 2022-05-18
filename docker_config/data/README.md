### Prerrequisites
To execute the script installerWindows.sh, we need:
 - A console which accept Linux commands and that will be able to access to the Windows environment variables (the git bash console for example).
 - To be located on the same directory than the script

### Variables
At the begining of the script, we can find these some variables, than let us to set which part of our application we need to mount:
 // deprecated: -buildimages -> 'true' if you want to generate docker images to resources
 // deprecated: - compileapps -> 'true' to compile Java, Vue and other programs before building the image 
 - installdb -> 'true' if you want to deploy InfluxDB in minikube
 - installapi -> 'true' to deploy influxdb api  to minikube
 - installback -> in development, doesn't works
 - installgrafana -> 'true' to deploy it to minikube
 - installkafka -> 'true' to deploy it to minikube
