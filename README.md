# RENERGETIC REPOSITORY STRUCTURE

## [Environments](environments/)
 Folder with the necessary files to deploy de application. The application can be deployed in a Openshift server or in your computer

### Configuration Files
 - [_installers.properties](environments/_installers.properties): File with variables to choose what and how deploy the application (it file is used both in PSNC the server and locally deployments)
 - [_credentials.properties](environments/_credentials.properties): File with PSNC credentials to allow deploy the application at server

### Local deployment files
 - [docker_config_local](environments/docker_config_local/): Necessary files to deploy the application at local computer
 - [deploy_local.sh](environments/deploy_local.sh): Script to deploy automatically the application at your local computer
 - [delete_local.sh](environments/delete_local.sh): Completely removes the local installation including configuration, saved passwords and database

### Openshift deployment files
 - [docker_config](environments/docker_config/): Necessary files to deploy the application at PSNC server
 - [deploy_psnc.sh](environments/deploy_psnc.sh): Script to deploy automatically the application at PSNC Openshift server

## [Configuration](configuration/)
 Folder with scripts to prepare test scenarios at the databases using the APIs, files with Nifi and Keycloak defaults configurations...

 - [relational_db](configuration/relational_db/): Contains a node script to create default scenarios, types and other data at PostgreSQL DB using the APIs and SQL scripts to generate data without APIs
 - [nifi](configuration/nifi/): Contains JSON files with Nifi templates
 - [keycloak](configuration/keycloak/): Contains JSON files with Keycloak Realms configuration, it can cause problems if you already had any change at Keycloak default configuration

## [Services](services/)
 Folder with Spring APIs

## [Front](front/)
 Folder to download the RENER_FRONT repository into it. The downloaded data will be used in *Enviroments* folder script to deploy the Front

 You can clone front repository from https://atlassian.gfi.es/bitbucket/scm/renergetic/rener_front.git

## [Execution](execution/)
Folder with scripts to allow access to Kubernetes services

