import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("plugin.jpa") version "1.2.71"
    id("org.springframework.boot") version "2.1.5.RELEASE"
    id("io.spring.dependency-management") version "1.0.7.RELEASE"
    id("org.flywaydb.flyway") version "5.2.4"
    id("org.jlleitschuh.gradle.ktlint") version "8.2.0"
    kotlin("jvm") version "1.2.71"
    kotlin("plugin.spring") version "1.2.71"
    kotlin("plugin.allopen") version "1.2.71"
    kotlin("kapt") version "1.2.71"
}

flyway {
    url = "jdbc:postgresql://" + System.getenv("DB_HOST") + "/" + System.getenv("DB_NAME")
    user = System.getenv("DB_USERNAME")
    password = System.getenv("DB_PASSWORD")
    outOfOrder = false
    baselineOnMigrate = true
}

group = "com.mazerunner"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.flywaydb:flyway-core")
    implementation("com.tinder.statemachine:statemachine:0.2.0")
    implementation("com.amazonaws:aws-java-sdk-s3:1.11.319")
    runtimeOnly("com.h2database:h2:1.4.197") // Fixed version as a workaround for https://github.com/h2database/h2database/issues/1841
    runtimeOnly("org.postgresql:postgresql")
    compile("com.graphql-java:graphql-spring-boot-starter:5.0.2")
    compile("com.graphql-java:graphql-java-tools:5.2.4")
    compile("com.graphql-java:graphiql-spring-boot-starter:5.0.2")
    runtimeOnly("org.springframework.boot:spring-boot-devtools")
    kapt("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "junit")
    }
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("com.ninja-squad:springmockk:1.1.2")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.Embeddable")
    annotation("javax.persistence.MappedSuperclass")
}

tasks.withType<Test> {
    useJUnitPlatform()
    systemProperty("spring.profiles.active", "test")
}
