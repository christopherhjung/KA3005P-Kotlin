plugins {
    java
    kotlin("jvm") version "1.7.10"
    `maven-publish`
}

group = "com.github.christopherhjung"
version = "1.0.0"

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>() {
    kotlinOptions.jvmTarget = "17"
}

repositories {
    mavenCentral()
    mavenCentral()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
    repositories {
        mavenLocal()
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.fazecast:jSerialComm:2.9.2")
}