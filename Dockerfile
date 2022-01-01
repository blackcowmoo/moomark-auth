# # Build spring
# FROM openjdk:11 as builder

# COPY . /spring
# WORKDIR /spring
# RUN ./gradlew build -x test -x testClasses -x checkstyleMain -x checkstyleTest

# RUN Spring
FROM openjdk:17-jre-slim

COPY ./build/libs/*.jar /spring/
WORKDIR /spring
RUN mv /spring/*.jar /spring/moomark.jar

ENV GOOGLE_CLIENT_ID '' \
    GOOGLE_CLIENT_SECRET '' \
    REDIS_HOST localhost \
    REDIS_PORT 6379 \
    REDIS_PASSWORD '' \
    MYSQL_URL jdbc:mysql://localhost:3306/db?serverTimezone=UTC&characterEncoding=UTF-8 \
    MYSQL_USERNAME '' \
    MYSQL_PASSWORD ''

EXPOSE 8080
STOPSIGNAL SIGINT

CMD ["java", "-jar", "moomark.jar"]
