FROM eclipse-temurin:17-jre

COPY ./build/libs/*.jar /spring/
WORKDIR /spring
RUN mv /spring/*.jar /spring/moomark.jar

EXPOSE 8080
STOPSIGNAL SIGINT

CMD ["java", "-jar", "moomark.jar"]
