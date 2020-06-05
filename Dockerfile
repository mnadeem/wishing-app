FROM openjdk:8-jdk-alpine
MAINTAINER Mohammad Nadeem
WORKDIR /app
COPY target/wishing-app*.jar /app/wishing-app.jar
ENTRYPOINT ["java", "-jar", "wishing-app.jar"]