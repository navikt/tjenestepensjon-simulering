plugins {
    java
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
    mavenLocal()
    maven("https://repo.adeo.no/repository/maven-releases/")
    maven("https://repo.adeo.no/repository/maven-snapshots/")
    maven("https://repo.adeo.no/repository/maven-central/")
    maven("http://repo.maven.apache.org/maven2")
}

dependencies {
    compile(kotlin("stdlib"))
    compile(kotlin("reflect"))
    compile("javax.xml.bind", "jaxb-api", "2.3.1")
    compile("com.nimbusds", "nimbus-jose-jwt", "2.10.1")
    compile("org.springframework","spring-context-support", "5.2.3.RELEASE")
    compile("org.springframework","spring-webflux", "5.2.3.RELEASE")
    compile("org.springframework.ws", "spring-ws-core", "3.0.8.RELEASE")
    compile("org.springframework.boot", "spring-boot-starter-web", "2.2.4.RELEASE")
    compile("org.springframework.security.oauth", "spring-security-oauth2", "2.3.6.RELEASE")
    compile("org.springframework.security", "spring-security-jwt", "1.0.10.RELEASE")
    compile("org.projectreactor", "reactor-spring", "1.0.1.RELEASE")
    compile("io.projectreactor.netty", "reactor-netty", "0.8.6.RELEASE")
    compile("io.micrometer", "micrometer-registry-prometheus", "1.1.4")
    implementation("com.fasterxml.jackson.core", "jackson-annotations", "2.10.2")
    implementation("com.github.ben-manes.caffeine", "caffeine", "2.7.0")
    testCompile("org.mockito", "mockito-junit-jupiter", "2.23.4")
    testCompile("com.github.tomakehurst", "wiremock-jre8", "2.23.2")
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
}


publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["java"])
        }
    }
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}


