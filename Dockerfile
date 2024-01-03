FROM ghcr.io/navikt/baseimages/temurin:21
COPY build/libs/tjenestepensjon-simulering.jar app.jar
COPY systemEnvironmentsMaskinporten.sh /init-scripts/systemEnvironmentsMaskinporten.sh
