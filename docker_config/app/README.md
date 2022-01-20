### Prerrequisites
To execute the script installerWindows.sh, we need:
 - A console which accept Linux commands and that will be able to access to the Windows environment variables (the git bash console for example).
 - To be located on the same directory than the script

### Variables
At the begining of the script, we can find these some variables, than let us to set which part of our application we need to mount:
 - buildimages -> 'true' if you want to generate docker images to resources
 - compileapps -> 'true' to compile Java, Vue and other programs before building the image 
 - installdb -> 'true' if you want to generate image and deploy postgres in minikube
 - installapi -> 'true' to deploy it to minikube
 - installapi1 -> 'true' to deploy it to minikube
 - installfront -> 'true' to deploy it to minikube
 - installkeycloak -> 'true' to deploy it to minikube
 - installwso -> 'true' to deploy it to minikube
 
 The script starts also minikube if needed