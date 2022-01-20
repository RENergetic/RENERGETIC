## INSTALL.SH
This script allow you to install the application in the OpenShift Server

### Prerrequisites
To execute the script installer.sh, we need:
 - A console which accept Linux commands and that will be able to access to the Windows environment variables (the git bash console for example).
 - To be located on the same directory than the script

### Variables
At the begining of the script, we can find two variables to set how we want install the application:
 - buildimages -> 'true' if you want to generate and push docker images. Is necessary if you want modify the image or you install the app for the first time but it takes a long time
 - compileapps -> 'true' if you have modified any app (API, Front or Keycloak themes). This option compile the app and prepare it to create a image (it's useless if the buildimages options is false)

### Options
	-a value --> The script is executing automatically, without user input. The allowed values to this option are:
			1 - To install app (PostgreSQL database, APIs to access to PostgreSQL, Keycloak and front)
			2 - To install data (InfluxDB, APIs to access to InfluxDB, Grafana and Nifi)
			3 - To install devops tools
			4 - To install app and data
	-b value --> Set buildimages to value
	-c value --> Set compileapps to value
	-h --------> Show this information

### Advices
You should revise the scripts data/installerWindows.sh,  app/installerWindows.sh and  devops/devopsWindows.sh to set that components you want to install

Also, you have to configure the file _credentials.properties with your user and token to access at the server

## PORT-FORWARD.SH
This script gives you access to installed server applications with a kubectl port-forward
You have to configure the file _credentials.properties with your user and token to access at the server

### Prerrequisites
To execute the script installer.sh, we need:
	- a console which accept Linux commands and that will be able to access to the Windows environment variables (the git bash console for example).
	- to be located on the same directory than the script

## DEVOPS-PORT-FORWARD.SH
This script gives you to access to installed server devops tools with a kubectl port-forward
You have to configure the file _credentials.properties with your user and token to access at the server

### Prerrequisites
To execute the script installer.sh, we need:
	- a console which accept Linux commands and that will be able to access to the Windows environment variables (the git bash console for example).
	- to be located on the same directory than the script