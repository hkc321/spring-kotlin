FROM openjdk:17
ARG JAR_FILE=*.jar
COPY spring-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar", "/app.jar"]