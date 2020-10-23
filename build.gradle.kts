import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "no.nav.tjenestepensjon"
version = "0.0.1-SNAPSHOT"
description = "tjenestepensjon-simulering"

plugins {
    kotlin("jvm") version "1.4.10"
    kotlin("plugin.spring") version "1.4.10"
    id("org.springframework.boot") version "2.3.4.RELEASE"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.github.com/navikt/maskinporten-client")
        credentials {
            username = "token"
            password = System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    implementation(kotlin("reflect"))
    implementation("com.fasterxml.jackson.module", "jackson-module-kotlin", "2.11.2")
    implementation("com.github.ben-manes.caffeine", "caffeine", "2.7.0")
    implementation("com.sun.xml.messaging.saaj", "saaj-impl", "1.5.1")
    implementation("io.micrometer", "micrometer-registry-prometheus", "1.5.5")
    implementation("javax.xml.bind", "jaxb-api", "2.3.1")
    implementation("javax.xml.soap", "javax.xml.soap-api", "1.4.0")
    implementation("no.nav.pensjonsamhandling", "maskinporten-client", "0.3.2")
    implementation("org.glassfish.jaxb", "jaxb-runtime", "2.3.2")
    implementation("org.springframework.boot", "spring-boot-actuator-autoconfigure")
    implementation("org.springframework.boot", "spring-boot-starter-cache")
    implementation("org.springframework.boot", "spring-boot-starter-web")
    implementation("org.springframework.boot", "spring-boot-starter-webflux")
    implementation("org.springframework.metrics", "spring-metrics", "0.5.1.RELEASE")
    implementation("org.springframework.security", "spring-security-jwt", "1.1.1.RELEASE")
    implementation("org.springframework.security.oauth", "spring-security-oauth2", "2.5.0.RELEASE")
            .exclude(group = "org.codehaus.jackson")
    implementation("org.springframework.ws", "spring-ws-core", "3.0.8.RELEASE")
    implementation("com.nimbusds", "nimbus-jose-jwt", "8.3")
    implementation("org.json", "json", "20190722")
    testImplementation(kotlin("test-junit5"))
    testImplementation("com.github.tomakehurst", "wiremock-jre8", "2.23.2")
    testImplementation("org.mockito", "mockito-junit-jupiter", "2.23.4")
    testImplementation("org.springframework.boot", "spring-boot-starter-test")
            .exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "14"
    }
    test {
        useJUnitPlatform()
    }
}


