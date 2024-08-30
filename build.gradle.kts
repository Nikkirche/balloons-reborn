import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.serialization") version "2.0.0"
    id("io.ktor.plugin") version "2.3.7"
}

tasks{
    withType<KotlinCompile> {
//        kotlinOptions {
//            freeCompilerArgs += listOf("-Xskip-prerelease-check")
//            jvmTarget = "21"
//        }
    }
}
group = "com.balloons"
version = "0.0.1"

application {
    mainClass.set("com.balloons.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")

}

repositories {
    repositories {
        maven("https://jitpack.io") {
            group = "com.github.icpc.live-v3"
        }
    }
    mavenCentral()
    maven(
        url = uri(
            "https://jitpack.io"
        )
    )
}
val exposedVersion: String by project
dependencies {
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-resources:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-crypt:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-json:$exposedVersion")
    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    implementation("com.github.icpc.live-v3:org.icpclive.cds.full:3.3.1")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")
    implementation("app.softwork:kotlinx-serialization-csv:0.0.18")

}

