plugins {
    id("java")
    id("io.gatling.gradle") version "3.9.5.6"
}

group = "se.edinjakupovic"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
}

