FROM maven:3.6.0-jdk-11-slim AS build

COPY src /home/app/src
COPY pom.xml /home/app

# Whenever in GCloud due to security purposes
# COPY .env /home/app

# Building the Service
RUN mvn -f /home/app/pom.xml clean package

FROM openjdk:11-jre-slim
# Getting the just created jar executable
COPY --from=build /home/app/target/BasicSpringAPI-0.0.1-SNAPSHOT.jar /usr/local/lib/app.jar

# Exposing it to port 8082
EXPOSE 8082
ENTRYPOINT ["java","-jar","/usr/local/lib/app.jar"]
