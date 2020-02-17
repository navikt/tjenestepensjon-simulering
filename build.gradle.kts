plugins {
    `maven-publish`
    kotlin("jvm") version "1.3.61"
    kotlin("plugin.spring") version "1.3.61"
    id("org.springframework.boot") version "2.2.4.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
}

group = "no.nav.tjenestepensjon"
version = "0.0.1-SNAPSHOT"
description = "tjenestepensjon-simulering"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("allopen"))
    implementation("com.fasterxml.jackson.core", "jackson-annotations", "2.10.2")
    implementation("com.fasterxml.jackson.module", "jackson-module-kotlin", "2.10.2")
    implementation("com.github.ben-manes.caffeine", "caffeine", "2.7.0")
    implementation("com.sun.xml.messaging.saaj", "saaj-impl", "1.5.1")
    implementation("io.micrometer", "micrometer-registry-prometheus", "1.1.4")
    implementation("io.projectreactor.netty", "reactor-netty", "0.8.6.RELEASE")
    implementation("javax.xml.bind", "jaxb-api", "2.3.1")
    implementation("javax.xml.soap", "javax.xml.soap-api", "1.4.0")
    implementation("org.glassfish.jaxb", "jaxb-runtime", "2.3.2")
    implementation("org.springframework","spring-webflux", "5.2.3.RELEASE")
    implementation("org.springframework.boot", "spring-boot-actuator-autoconfigure", "2.2.4.RELEASE")
    implementation("org.springframework.boot", "spring-boot-starter-cache", "2.2.4.RELEASE")
    implementation("org.springframework.boot", "spring-boot-starter-web", "2.2.4.RELEASE")
    implementation("org.springframework.metrics", "spring-metrics", "0.5.1.RELEASE")
    implementation("org.springframework.security", "spring-security-jwt", "1.0.10.RELEASE")
    implementation("org.springframework.security.oauth", "spring-security-oauth2", "2.3.6.RELEASE")
            .exclude(group = "org.codehaus.jackson")
    implementation("org.springframework.ws", "spring-ws-core", "3.0.8.RELEASE")
    testImplementation(kotlin("test-junit5"))
    testImplementation("com.github.tomakehurst", "wiremock-jre8", "2.23.2")
    testImplementation("org.mockito", "mockito-junit-jupiter", "2.23.4")
    testImplementation("org.springframework.boot", "spring-boot-starter-test", "2.2.4.RELEASE")
}


publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["java"])
        }
    }
}

tasks {
    withType<Test>{
        useJUnitPlatform()
    }
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}


