plugins {
  id("org.springframework.boot") version "2.6.2"
  id("io.spring.dependency-management") version "1.0.11.RELEASE"
  id("org.jetbrains.kotlin.jvm") version "1.6.10"
  id("java")
  id("jacoco")
  id("checkstyle")
  id("org.jlleitschuh.gradle.ktlint") version "10.2.1"
}

group = "com.blackcowmoo.moomark"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
  compileOnly {
    extendsFrom(configurations.annotationProcessor.get())
  }
}

repositories {
  mavenCentral()
}

sourceSets {
  main {
    java.srcDir(file("src/main/kotlin"))
  }
  test {
    java.srcDir(file("src/test/kotlin"))
  }
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  compileOnly("org.projectlombok:lombok")
  runtimeOnly("com.h2database:h2")
  runtimeOnly("mysql:mysql-connector-java:8.0.27")
  annotationProcessor("org.projectlombok:lombok")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testRuntimeOnly("com.h2database:h2")
  testImplementation("org.mockito:mockito-inline:4.0.0")
  implementation("io.jsonwebtoken:jjwt-api:0.11.2")
  runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.2")
  runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.2")
}

tasks.test {
  useJUnitPlatform()
  finalizedBy(tasks.jacocoTestReport)
}

tasks.jar {
  enabled = false
}

jacoco {
  toolVersion = "0.8.8"
}

tasks.jacocoTestReport {
  dependsOn(tasks.test)
  reports {
    xml.required.set(true)
    html.required.set(true)
  }
}

tasks.bootJar {
  mainClass.set("com.blackcowmoo.moomark.auth.AuthApplicationKt")
}
