import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "1.9.22"
    id("io.ktor.plugin") version "2.3.7"
}

tasks{
    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs += listOf("-Xskip-prerelease-check")
        }
    }
}
group = "com.ballons"
version = "0.0.1"

application {
    mainClass.set("com.ballons.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    repositories {
        maven("https://maven.pkg.github.com/icpc/live-v3/") {
            group = "org.icpclive"
            credentials {
                username = System.getenv("GIT_USER")!!
                password = System.getenv("GIT_PASS")!!
            }
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
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-crypt:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-json:$exposedVersion")
    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    implementation("org.icpclive:org.icpclive.cds.full:+")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.4.0")

}

