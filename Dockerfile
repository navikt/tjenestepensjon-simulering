FROM navikt/java:16
COPY build/libs/*.jar app.jar
COPY systemEnvironmentsMaskinporten.sh /init-scripts/systemEnvironmentsMaskinporten.sh