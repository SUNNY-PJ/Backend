FROM openjdk:17-jdk-slim
ARG JAR_FILE=./build/libs/Backend-0.0.1-SNAPSHOT.jar
ARG YML_FILE=./src/main/resources/application-local.yml
COPY ${JAR_FILE} app.jar
COPY ${YML_FILE} application-local.yml
ENTRYPOINT ["java","-jar","/app.jar"]