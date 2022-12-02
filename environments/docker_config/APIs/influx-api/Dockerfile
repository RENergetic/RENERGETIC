FROM eclipse-temurin:11-jre-alpine
ARG JAR_FILE=*.jar
COPY ${JAR_FILE} application.jar
ENTRYPOINT ["java", "-Xmx256m", "-jar", "application.jar"]
