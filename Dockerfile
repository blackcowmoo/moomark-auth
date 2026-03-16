ARG BUILD_DATE
ARG VCS_REF

FROM gradle:8.5-jdk17 AS builder
WORKDIR /build
COPY gradle/ gradle/
COPY gradlew ./
COPY build.gradle.kts settings.gradle.kts ./
COPY src/ src/
RUN chmod +x gradlew && ./gradlew bootJar --no-daemon \
    && rm -rf /build/.gradle \
    && find /build -name "*.class" -delete

FROM eclipse-temurin:17-jre
ARG BUILD_DATE
ARG VCS_REF

LABEL org.opencontainers.image.created="${BUILD_DATE}" \
      org.opencontainers.image.revision="${VCS_REF}" \
      org.opencontainers.image.version="1.0.0"

WORKDIR /app
COPY --from=builder /build/build/libs/*.jar /app/moomark.jar
EXPOSE 8080
STOPSIGNAL SIGINT
CMD ["java", "-jar", "moomark.jar"]
