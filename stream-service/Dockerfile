FROM eclipse-temurin:24-jdk-noble AS builder
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

COPY src src
RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:24-jre-alpine AS image

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

COPY --from=builder --chown=appuser:appgroup /app/build/libs/*.jar app.jar

RUN mkdir -p /app/logs app/tmp && chown -R appuser:appgroup /app && chmod -R 750 /app && chmod 644 /app/app.jar

USER appuser

ENV JAVA_OPTS="-XX:TieredStopAtLevel=1 -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

EXPOSE 8008

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]