# CONFIGURATION

 Folder with scripts to prepare test scenarios at the databases using the APIs, files with Nifi and Keycloak defaults configurations...

 - [relational_db](relational_db/): Contains a node script to create default scenarios, types and other data at PostgreSQL DB using the APIs and SQL scripts to generate data without APIs
 - [nifi](nifi/): Contains JSON files with Nifi templates
 - [keycloak](keycloak/): Contains JSON files with Keycloak Realms configuration, it can cause problems if you already had any change at Keycloak default configuration

## [Relational_db](relational_db/)

Contains files to insert data into Renergetic relational database

 - [APIs](relational_db/APIs/): Contains a Node script to insert data into relational database using Renergetic APIs
 - [SQL](relational_db/SQL/): Contains some SQL scripts to insert data into relational database, this scripts should be executed into DB

## [Nifi](nifi/)

Contains JSON files with NIFI templates

 - [DummyDataGenerator](nifi/DummyDataGenerator.xml): Template to insert dummy data into InfluxDB < 2.0
 - [DummyDataGeneratorAPI](nifi/DummyDataGeneratorAPI.xml): Template insert dummy data into InfluxDB using a API
 - [FlowFilesGenerator](nifi/FlowFilesGenerator.xml): Template with a random flowfiles generatorº
 - [Renergetic](nifi/renergetic.xml): Template to insert data into influx from CSV file
 - [RenergeticFTP](nifi/renergeticFTP.xml): Template to insert data into influx from CSV file from a FTP server
