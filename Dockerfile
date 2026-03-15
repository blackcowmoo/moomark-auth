FROM gradle:8.5-jdk17 AS builder
WORKDIR /build
COPY gradle/ gradle/
COPY build.gradle.kts settings.gradle.kts ./
COPY src/ src/
RUN gradle bootJar --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /build/build/libs/*.jar /app/moomark.jar
EXPOSE 8080
STOPSIGNAL SIGINT
CMD ["java", "-jar", "moomark.jar"]
