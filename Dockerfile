FROM navikt/java:12
COPY build/libs/*.jar /app/app.jar
