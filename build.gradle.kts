import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "no.nav.tjenestepensjon"
description = "tjenestepensjon-simulering"

plugins {
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
    id("org.springframework.boot") version "2.7.0"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
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
    implementation("com.fasterxml.jackson.module", "jackson-module-kotlin", "2.13.3")
    implementation("com.github.ben-manes.caffeine", "caffeine", "3.1.1")
    implementation("com.microsoft.azure", "msal4j", "1.12.0")
    implementation("com.sun.xml.messaging.saaj", "saaj-impl", "1.5.1")
    implementation("io.micrometer", "micrometer-registry-prometheus")
    implementation("javax.xml.bind", "jaxb-api", "2.3.1")
    implementation("javax.xml.soap", "javax.xml.soap-api", "1.4.0")
    implementation("net.logstash.logback", "logstash-logback-encoder", "7.2")
    implementation("no.nav.pensjonsamhandling", "maskinporten-client", "1.0.1")
    implementation("org.glassfish.jaxb", "jaxb-runtime", "2.3.2")
    implementation("org.springframework.boot", "spring-boot-starter-actuator")
    implementation("org.springframework.boot", "spring-boot-starter-cache")
    implementation("org.springframework.boot", "spring-boot-starter-web")
    implementation("org.springframework.boot", "spring-boot-starter-webflux")
    implementation("org.springframework.boot", "spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.ws", "spring-ws-core", "3.1.3")
    implementation("org.json", "json", "20220320")
    testImplementation(kotlin("test-junit5"))
    testImplementation("com.github.tomakehurst", "wiremock-jre8", "2.33.2")
    testImplementation("org.mockito", "mockito-junit-jupiter", "4.6.0")
    testImplementation("org.springframework.boot", "spring-boot-starter-test")
    testImplementation("org.springframework.security", "spring-security-test")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }
    test {
        useJUnitPlatform()
    }
}


