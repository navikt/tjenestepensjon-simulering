import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "no.nav.tjenestepensjon"
description = "tjenestepensjon-simulering"

plugins {
    kotlin("jvm") version "1.6.0"
    kotlin("plugin.spring") version "1.6.0"
    id("org.springframework.boot") version "2.6.1"
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
    implementation("com.fasterxml.jackson.module", "jackson-module-kotlin", "2.13.0")
    implementation("com.github.ben-manes.caffeine", "caffeine", "2.7.0")
    implementation("com.sun.xml.messaging.saaj", "saaj-impl", "1.5.1")
    implementation("io.micrometer", "micrometer-registry-prometheus")
    implementation("javax.xml.bind", "jaxb-api", "2.3.1")
    implementation("javax.xml.soap", "javax.xml.soap-api", "1.4.0")
    implementation("no.nav.pensjonsamhandling", "maskinporten-client", "1.0.0")
    implementation("org.glassfish.jaxb", "jaxb-runtime", "2.3.2")
    implementation("org.springframework.boot", "spring-boot-starter-actuator")
    implementation("org.springframework.boot", "spring-boot-starter-cache")
    implementation("org.springframework.boot", "spring-boot-starter-web")
    implementation("org.springframework.boot", "spring-boot-starter-webflux")
    implementation("org.springframework.security", "spring-security-jwt", "1.1.1.RELEASE")
    implementation("org.springframework.security.oauth", "spring-security-oauth2", "2.5.1.RELEASE")
            .exclude(group = "org.codehaus.jackson")
    implementation("org.springframework.ws", "spring-ws-core", "3.1.1")
    implementation("com.nimbusds", "nimbus-jose-jwt", "9.15.2")
    implementation("org.json", "json", "20190722")
    testImplementation(kotlin("test-junit5"))
    testImplementation("com.github.tomakehurst", "wiremock-jre8", "2.32.0")
    testImplementation("org.mockito", "mockito-junit-jupiter", "2.23.4")
    testImplementation("org.springframework.boot", "spring-boot-starter-test")
            .exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "16"
    }
    test {
        useJUnitPlatform()
    }
}


