### Prerrequisites
To execute the script installerWindows.sh, we need:
 - A console which accept Linux commands and that will be able to access to the Windows environment variables (the git bash console for example).
 - To be located on the same directory than the script

### Variables
At the begining of the script, we can find these some variables, than let us to set which part of our application we need to mount:
 - installsonarqube -> 'true' if you want to deploy postgres in minikube
 - installjenkins -> 'true' to deploy it to minikube
 
 The script starts also minikube if needed