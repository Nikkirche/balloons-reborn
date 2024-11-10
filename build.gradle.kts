import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jooq.meta.jaxb.Logging

val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project

plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.serialization") version "2.0.21"
    id("io.ktor.plugin") version "2.3.7"
    id("nu.studer.jooq") version "9.0"
}

tasks{
    withType<KotlinCompile> {
        compilerOptions {
            // freeCompilerArgs += listOf("-Xskip-prerelease-check")
            jvmTarget.set(JvmTarget.JVM_21)
        }
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
    mavenCentral()
    maven("https://jitpack.io") {
        group = "com.github.icpc.live-v3"
    }
}

val exposedVersion: String by project
dependencies {
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-resources:$ktorVersion")
    implementation("io.ktor:ktor-server-websockets:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("com.github.icpc.live-v3:org.icpclive.cds.full:3.3.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")
    implementation("org.jooq:jooq:3.19.15")
    implementation("com.h2database:h2:2.3.232")
    implementation("org.jooq:jooq-kotlin:3.19.15")

    jooqGenerator("com.h2database:h2:2.3.232")

    testImplementation("io.ktor:ktor-server-tests-jvm:$ktorVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")

    // Deprecated
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-crypt:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-json:$exposedVersion")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.4.0")
    implementation("app.softwork:kotlinx-serialization-csv:0.0.18")
}

jooq {
    version = "3.19.1"

    configurations {
        create("main") {
            generateSchemaSourceOnCompilation = true
            jooqConfiguration.apply {
                logging = Logging.WARN
                jdbc.apply {
                    driver = "org.h2.Driver"
                    url = "jdbc:h2:mem:"
                }
                generator.apply {
                    database.apply {
                        inputSchema = "PUBLIC"
                        includes = ".*"
                    }
                    target.apply {
                        packageName = "org.icpclive.balloons.db.generated"
                        directory = "./src/generated"
                    }
                }
            }
        }
    }
}