plugins {
	java
	id("org.springframework.boot") version "3.1.4"
	id("io.spring.dependency-management") version "1.1.3"
}

group = "se.edinjakupovic"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-actuator")

	implementation("org.flywaydb:flyway-core:9.22.2")
	implementation("org.flywaydb:flyway-mysql:9.22.2")

	implementation("org.jetbrains:annotations:24.0.0")
	implementation("com.github.f4b6a3:ulid-creator:5.2.2")
	implementation("io.micrometer:micrometer-registry-prometheus:1.11.4")


	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("com.mysql:mysql-connector-j")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.testcontainers:testcontainers:1.19.1")
	testImplementation("org.testcontainers:mysql:1.19.1")
	testImplementation("org.testcontainers:junit-jupiter:1.19.1")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
