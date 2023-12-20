
# Archetype: jpa-api

Project to generate an archetype to generate new RENergetic APIs

This project contains:
 - Swagger and open API documentation (with necessary libraries, configuration and examples)
 - Configured JPA prepared to be connected with H2 and PostgreSQL databases
 - The RENergetic common library (WARN: this library entities are compatible with PostgreSQL databases only)
 - Examples of controllers, entities, services and the package structure
 - Spring Security prepared to securize each API endpoint (should be uncommented and configured on [WebSecurityConfig](src/main/java/com/renergetic/api/config/WebSecurityConfig.java))

The API can be configured at [application-*.properties](src/main/resources)

## Table of contents

- [Archetype: jpa-api](#archetype-jpa-api)
  - [Table of contents](#table-of-contents)
  - [User Guide](#user-guide)
  - [Installation](#installation)
  - [Configuration](#configuration)
  - [Usage](#usage)

## User Guide

## Installation

 - Generate archetype from the project (Configure version at [archetype.properties](archetype.properties))
   - `mvn -U clean archetype:create-from-project -Dinteractive=false -DkeepParent=true -DpropertyFile="archetype.properties"`
 - The archetype will be generated at [./target/generated-sources/archetype](target/generated-sources/archetype/) and you should install it from there
   - `cd ./target/generated-sources/archetype`
 - Install it in local Maven
   - `mvn -B -U clean install`
 - Install it at Nexus repository (You should configure POM and Maven*)
   - `mvn -B -U clean deploy`

* For configure add the follow lines on the archetype POM (The Nexus user and password should be configured at global Maven settings)

```
  <distributionManagement>
    <repository>
      <id>mvn-newsop-release-virtual</id>
      <url>https://nexus-ren-prototype-devops.apps.paas-dev.psnc.pl//repository/renergetic-release/</url>
    </repository>
  </distributionManagement>
```

## Configuration

The configuration is set in the file [application.properties](./src/main/resources/application.properties).

In addition, you can select default configurations located in the following files:
- [application-dev.properties](./src/main/resources/application-dev.properties) - Development environment configuration (It have a embebed H2 database by default, the RENergetic library entities aren't compatible with it)
- [application-prod.properties](./src/main/resources/application-prod.properties) - Production environment configuration

## Usage

- Once installed locally or in a remote repository to which you have a connection, execute the following command:
   - `mvn archetype:generate -DarchetypeGroupId="com.renergetic" -DarchetypeArtifactId="jpa-api" -DarchetypeVersion="INSTALLED_VERSION"`
- If you want to list all available archetypes, run the following command:
   - `mvn archetype:generate`
- If you don't know the installed version, check the configuration in the [archetype.properties](archetype.properties) file, which contains all the archetype details.
