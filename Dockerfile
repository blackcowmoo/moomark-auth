FROM alpine as base

RUN apk add --no-cache openjdk17-jre

###########
FROM base

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
