
# User API

API to manage RENergetic users

This project contains:
 - Swagger and open API documentation (with necessary libraries, configuration and examples)
 - Configured JPA prepared to be connected with H2 and PostgreSQL databases
 - The RENergetic common library (WARN: this library entities are compatible with PostgreSQL databases only)
 - Examples of controllers, entities, services and the package structure
 - Spring Security prepared to securize each API endpoint (should be uncommented and configured on [WebSecurityConfig](src/main/java/com/renergetic/api/config/WebSecurityConfig.java))

The API can be configured at [application-*.properties](src/main/resources)

## Table of contents

- [User API](#user-api)
  - [Table of contents](#table-of-contents)
  - [User Guide](#user-guide)
  - [Configuration](#configuration)
  - [Usage](#usage)

## User Guide

## Configuration

The configuration is set in the file [application.properties](./src/main/resources/application.properties).

In addition, you can select default configurations located in the following files:
- [application-dev.properties](./src/main/resources/application-dev.properties) - Development environment configuration (It have a embebed H2 database by default, the RENergetic library entities aren't compatible with it)
- [application-prod.properties](./src/main/resources/application-prod.properties) - Production environment configuration

## Usage

- Check that the application properties are properly configured

Check if there are the services properly configured
 - PostgreSQL with the RENergetic RBD schema
 - Keycloak with admin client and the user and password configured on properties

Compile the API
 - `mvn clean package`

Go to target directory and run the JAR:
 - `java -jar project_name.jar`
