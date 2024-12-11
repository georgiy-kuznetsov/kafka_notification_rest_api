## Build stage
FROM gradle:jdk21-jammy AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle clean build -x test --no-daemon


## Package stage
FROM openjdk:21-jdk-slim
RUN mkdir /app
ENV PORT=8088
WORKDIR /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/kafkanotificationrestapi-1.0.0.jar
ENTRYPOINT ["java", "-jar", "/app/kafkanotificationrestapi-1.0.0.jar"]