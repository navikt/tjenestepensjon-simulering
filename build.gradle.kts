group = "no.nav.tjenestepensjon"
description = "tjenestepensjon-simulering"

plugins {
    kotlin("jvm") version "1.9.20"
    kotlin("plugin.spring") version "1.9.22"
    id("org.springframework.boot") version "3.1.5"
    id("io.spring.dependency-management") version "1.1.4"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("reflect"))
    implementation("com.fasterxml.jackson.module", "jackson-module-kotlin", "2.15.0")
    implementation("com.github.ben-manes.caffeine", "caffeine", "3.1.1")
    implementation("com.microsoft.azure", "msal4j", "1.12.0")
    implementation("com.sun.xml.messaging.saaj", "saaj-impl", "3.0.1")
    implementation("io.micrometer", "micrometer-registry-prometheus")
    implementation("jakarta.annotation", "jakarta.annotation-api", "2.1.1")
    implementation("javax.xml.soap", "javax.xml.soap-api", "1.4.0")
    implementation("net.logstash.logback", "logstash-logback-encoder", "7.2")
    implementation("org.glassfish.jaxb", "jaxb-runtime", "4.0.1")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.15.2")
    implementation("org.springframework.boot", "spring-boot-starter-actuator")
    implementation("org.springframework.boot", "spring-boot-starter-cache")
    implementation("org.springframework.boot", "spring-boot-starter-web")
    implementation("org.springframework.boot", "spring-boot-starter-webflux")
    implementation("org.springframework.boot", "spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.ws", "spring-ws-core", "4.0.3")
    implementation("org.json", "json", "20220320")
    testImplementation(kotlin("test-junit5"))
    testImplementation("com.github.tomakehurst:wiremock:3.0.1")
    testImplementation("org.springframework.boot", "spring-boot-starter-test")
    testImplementation("org.springframework.security", "spring-security-test")
    implementation("org.springframework.boot:spring-boot-properties-migrator:3.2.2")

}

tasks {
    test {
        useJUnitPlatform()
    }
}


