FROM eclipse-temurin:17-jre-alpine
ARG JAR_FILE=*.jar
COPY ${JAR_FILE} application.jar
ENTRYPOINT ["java", "-Xms950m", "-Xmx975m", "-jar", "application.jar"]
