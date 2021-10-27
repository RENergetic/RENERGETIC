*Before to execute this script you need to have docker and minikube installed


Execute installerWindows.sh from a Linux console able to access to the Win environment variables (GIT bash for example)
Do the execution on the directory of the file
At the begining of the script, you can stablish some variables:
 - java -> path where the Spring project is
 - javafile -> jar name after the maven execution
 - installdb -> 'true' if you want to generate image and deploy postgres in minikube, 'false' if you don't need it
 - installapi -> 'true' to compile, generate image and deploy it to minikube
 
The script starts also minikube if needed
 