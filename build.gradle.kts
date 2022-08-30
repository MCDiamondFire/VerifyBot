import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    kotlin("plugin.serialization") version "1.7.10"

    id("com.github.johnrengelman.shadow") version "7.1.2"
    application
}

application {
    mainClass.set("com.mcdiamondfire.verifybot.VerifyBotKt")
}

group = "com.mcdiamondfire"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val exposedVersion: String by project
val ktomlVersion: String by project

dependencies {
    implementation("ch.qos.logback", "logback-classic", "1.2.8")

    implementation("net.dv8tion", "JDA", "5.0.0-alpha.18") {
        exclude(module = "opus-java")
    }

    implementation("mysql", "mysql-connector-java", "8.0.30")

    implementation("com.akuleshov7", "ktoml-core", ktomlVersion)
    implementation("com.akuleshov7", "ktoml-file", ktomlVersion)

    implementation("org.jetbrains.exposed", "exposed-core", exposedVersion)
    implementation("org.jetbrains.exposed", "exposed-dao", exposedVersion)
    implementation("org.jetbrains.exposed", "exposed-jdbc", exposedVersion)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<ShadowJar> {
    archiveFileName.set("VerifyBot.jar")
}
