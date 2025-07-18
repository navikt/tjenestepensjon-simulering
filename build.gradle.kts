group = "no.nav.tjenestepensjon"
description = "tjenestepensjon-simulering"

plugins {
    kotlin("jvm") version "2.2.0"
    kotlin("plugin.spring") version "2.2.0"
    id("org.springframework.boot") version "3.5.3"
    id("io.spring.dependency-management") version "1.1.7"
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
    implementation("net.logstash.logback", "logstash-logback-encoder", "8.0")
    implementation("ch.qos.logback.access:logback-access-common:2.0.6")
    implementation("ch.qos.logback:logback-core:1.5.18")
    implementation("ch.qos.logback.access:logback-access-tomcat:2.0.6") {
        exclude("org.apache.tomcat","tomcat-catalina") //classes supplied by spring-boot (tomcat-embed-core) use a newer version (10.1.26 vs 10.0.27 supplied here)
        exclude("org.apache.tomcat","tomcat-coyote") //classes supplied by spring-boot (tomcat-embed-core) use a newer version (10.1.26 vs 10.0.27 supplied here)
    }
    implementation("org.yaml","snakeyaml","2.3")
    implementation("org.codehaus.janino:janino:3.1.12")
    implementation("org.codehaus.janino:commons-compiler:3.1.12")
    implementation ("io.github.oshai", "kotlin-logging-jvm", "5.1.0")
    implementation("org.slf4j","slf4j-api", "2.0.12" )
    implementation("org.glassfish.jaxb", "jaxb-runtime", "4.0.1")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.19.1")
    implementation("io.getunleash:unleash-client-java:11.0.2")
    implementation("org.springframework.boot:spring-boot-starter-tomcat") // Spring Boot manages Tomcat

    // Exclude older versions of Tomcat
    implementation("org.apache.tomcat.embed:tomcat-embed-core:11.0.8")
    implementation("org.springframework.boot", "spring-boot-starter-actuator")
    implementation("org.springframework.boot", "spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot", "spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.ws", "spring-ws-core", "4.0.11")
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.wiremock:wiremock-standalone:3.13.1")
    testImplementation("org.mockito.kotlin", "mockito-kotlin", "5.2.1")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.9.1")
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.9.1")
    testImplementation("org.springframework.boot", "spring-boot-starter-test")
    testImplementation("org.springframework.security", "spring-security-test")
}

tasks {
    test {
        useJUnitPlatform()
        jvmArgs("-Xshare:off")
    }
}
