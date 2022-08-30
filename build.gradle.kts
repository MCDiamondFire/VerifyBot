import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    kotlin("plugin.serialization") version "1.7.10"
}

group = "com.mcdiamondfire"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val exposedVersion: String by project
val ktomlVersion: String by project

dependencies {
    testImplementation(kotlin("test"))

    implementation("net.dv8tion", "JDA", "5.0.0-alpha.18") {
        exclude(module = "opus-java")
    }

    implementation("mysql:mysql-connector-java:8.0.30")

    implementation("com.akuleshov7", "ktoml-core", ktomlVersion)
    implementation("com.akuleshov7", "ktoml-file", ktomlVersion)

    implementation("org.jetbrains.exposed", "exposed-core", exposedVersion)
    implementation("org.jetbrains.exposed", "exposed-dao", exposedVersion)
    implementation("org.jetbrains.exposed", "exposed-jdbc", exposedVersion)
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
