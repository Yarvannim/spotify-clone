FROM --platform=$BUILDPLATFORM gradle:8.10.2-jdk24-alpine AS builder
WORKDIR /app

COPY build.gradle settings.gradle gradle.properties ./
COPY gradle/wrapper gradle/wrapper

RUN ./gradlew dependencies --no-daemon

COPY src ./src

RUN ./gradlew bootJar --no-daemon

RUN java -Djarmode=layertools -jar build/libs/*.jar --destination extracted

FROM eclipse-temurin:24-jre-alpine AS image

RUN addgroup -S spring && adduser -S spring -G spring

WORKDIR /app

COPY --from=builder --chown=spring:spring /app/extracted/dependencies/ ./
COPY --from=builder --chown=spring:spring /app/extracted/spring-boot-loader/ ./
COPY --from=builder --chown=spring:spring /app/extracted/snapshot-dependencies/ ./
COPY --from=builder --chown=spring:spring /app/extracted/application/ ./

USER spring

EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:+UseZGC", "-Djava.security.egd=file:/dev/./urandom", "org.springframework.boot.loader.JarLauncher"]