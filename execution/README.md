# EXECUTION

Folder with scripts to allow access to Kubernetes services

## [Port Forward](port_forward.sh): 

Script access to Kubernetes services in your Minikube local installation. To execute it you should have Docker Desktop started

You should configure the namespace where your applications are deployed, by default *ren-prototype*, this can be changed into a variable at the first lines of the script

To execute it use a CLI as Git Bash and run the command `./port_forward.sh` in this folder

The script ask you which service you want to open and a port to open it. Please note that only can be open a service simultaneously in a same port and you should have the services running to access to it. If you want know how services are running check it using `minikube dashboard`
