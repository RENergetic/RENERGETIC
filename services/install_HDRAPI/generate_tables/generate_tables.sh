# This script compile and run HDR-API
current=$(pwd -W tr / \\)
javafilehdr='hdrapi-0.0.1-SNAPSHOT.jar'
cd "${current}\\..\\..\\hdrapi"

# COPY CONFIGURATION
cp "${current}\\pom.xml" ".\\pom.xml"

# GENERATE TABLES
mvn clean install -Preverse -Dmaven.test.skip
rm -rf "${current}\\generated"
cp -r ".\\src\\main\\java\\com\\renergetic\\backdb\\model\\test" "${current}\\generated"
