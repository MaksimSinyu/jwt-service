FROM openjdk:17-jdk-alpine

ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS \
    SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS=""

WORKDIR /app

COPY jwt-service-1.0.0.jar app.jar

EXPOSE 8080

RUN mkdir -p /app/logs

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]