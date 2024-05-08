FROM eclipse-temurin:17-jre-alpine
ARG JAR_FILE=*.jar
COPY ${JAR_FILE} application.jar
ENTRYPOINT ["java", "-Xms1024m", "-Xmx1024m", "-jar", "application.jar"]
