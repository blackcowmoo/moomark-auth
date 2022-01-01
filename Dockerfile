FROM alpine as base

RUN apk add --no-cache openjdk17-jre

###########
FROM base

COPY ./build/libs/*.jar /spring/
WORKDIR /spring
RUN mv /spring/*.jar /spring/moomark.jar

EXPOSE 8080
STOPSIGNAL SIGINT

CMD ["java", "-jar", "moomark.jar"]
