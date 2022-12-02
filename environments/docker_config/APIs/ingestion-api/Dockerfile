FROM adoptopenjdk:11-jre-hotspot
ARG JAR_FILE=*.jar
COPY ${JAR_FILE} application.jar
ENTRYPOINT ["java", "-Xmx256m", "-jar", "application.jar"]
