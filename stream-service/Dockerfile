FROM eclipse-temurin:24-jdk AS builder
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

COPY src src

RUN ./gradlew bootJar

FROM eclipse-temurin:24-jre-alpine AS image

ARG JAR_FILE=/app/build/libs/*.jar

WORKDIR /app

COPY --from=builder ${JAR_FILE} app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-XX:TieredStopAtLevel=1", "-jar", "app.jar"]