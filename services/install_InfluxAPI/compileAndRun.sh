# This script compile and run HDR-API
current=$(pwd -W tr / \\)
javafilehdr='measurementapi-0.0.1-SNAPSHOT.jar'
cd "${current}\\..\\measurementapi"

# COPY PROPERTIES
cp "${current}\\application.properties" ".\\src\\main\\resources\\application.properties"

# API COMPILE TO JAR
mvn clean package -Dmaven.test.skip
cp ".\\target\\${javafilehdr}" "${current}\\application.jar"

# RUN API
cd "${current}"
java -jar application.jar
