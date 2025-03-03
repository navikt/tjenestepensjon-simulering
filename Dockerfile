FROM gcr.io/distroless/java21-debian12:nonroot
WORKDIR /app
ENV TZ="Europe/Oslo"
COPY build/libs/tjenestepensjon-simulering.jar app.jar
EXPOSE 8080
CMD ["app.jar"]
