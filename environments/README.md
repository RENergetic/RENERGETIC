# ENVIRONMENTS

Contains scripts to deploy Renergetic services and UI in a local environment or in Openshift server

# BEFORE OF DEPLOY

To deploy the environments your PC should meet some requirements:

 - [Docker Desktop](https://www.docker.com/products/docker-desktop/) installed and running
 - [Minikube](https://minikube.sigs.k8s.io/docs/start/) installed
 - [Maven](https://maven.apache.org/install.html) installed
 - [OC command line](https://github.com/openshift/origin/releases/tag/v3.11.0) (To PSNC deployment)

See [more information](https://atlassian.gfi.es/confluence/display/RENERGETIC/Dockerized+HelloWorld+-+AUTOMATED+process)

# HOW TO DEPLOY A LOCAL ENVIRONMENT

 - Open [_installer.properties](_installers.properties) file
 - Set a *name* to the kubernetes namespace (`project` variable)
 - Set your APIs and UI folders or set it empty to use default folders
 - Set to *true* the components that you want to install
 - Execute [deploy_local.sh](deploy_local.sh) in a Git Bash or other CLI that allow execute Linux commands

# HOW TO DEPLOY A PSNC ENVIRONMENT

 - Open [_credentials_.properties](_credentials.properties) file
 - Set your user and password
 - Open [_installer.properties](_installers.properties) file
 - Set your Openshif project
 - Set your APIs and UI folders or set it empty to use default folders
 - Set to *true* the components that you want to install
 - Execute [deploy_psnc.sh](deploy_psnc.sh) in a Git Bash or other CLI that allow execute Linux commands

