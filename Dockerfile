FROM navikt/java:14
COPY build/libs/*.jar app.jar
COPY systemEnvironmentsMaskinporten.sh /init-scripts/systemEnvironmentsMaskinporten.sh