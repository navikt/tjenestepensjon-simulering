FROM navikt/java:18
COPY build/libs/tjenestepensjon-simulering.jar app.jar
COPY systemEnvironmentsMaskinporten.sh /init-scripts/systemEnvironmentsMaskinporten.sh
