FROM navikt/java:17
COPY build/libs/tjenestepensjon-simulering.jar app.jar
COPY systemEnvironmentsMaskinporten.sh /init-scripts/systemEnvironmentsMaskinporten.sh
