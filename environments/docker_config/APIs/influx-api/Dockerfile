FROM eclipse-temurin:17-jre-alpine
ARG JAR_FILE=*.jar
COPY ${JAR_FILE} application.jar
ENTRYPOINT ["java", "-Xms1450m", "-Xmx1475m", "-jar", "application.jar"]
