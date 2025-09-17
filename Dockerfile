FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre:openjdk-21
WORKDIR /app
ENV TZ="Europe/Oslo"
COPY build/libs/tjenestepensjon-simulering.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Xshare:off", "-jar", "app.jar"]
