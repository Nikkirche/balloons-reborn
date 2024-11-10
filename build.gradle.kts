import org.jooq.meta.jaxb.Logging

plugins {
    java
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ktor)
    alias(libs.plugins.jooq)
}

group = "org.icpclive.balloons"
version = "0.0.1"

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("org.icpclive.balloons.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
    maven("https://jitpack.io") {
        group = "com.github.icpc.live-v3"
    }
}

dependencies {
    implementation(libs.bundles.koin)
    implementation(libs.bundles.ktor)
    implementation(libs.logback)
    implementation(libs.live.cds)
    implementation(libs.kotlin.serialization.json)
    implementation(libs.h2)
    implementation(libs.jooq.kotlin)

    jooqGenerator(libs.h2)

    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.launcher)
}

jooq {
    version = libs.versions.jooq

    configurations {
        create("main") {
            generateSchemaSourceOnCompilation
            generateSchemaSourceOnCompilation = true
            jooqConfiguration.apply {
                logging = Logging.WARN
                jdbc.apply {
                    driver = "org.h2.Driver"
                    url = "jdbc:h2:mem:;INIT=RUNSCRIPT FROM './src/main/resources/schema.sql'"
                }
                generator.apply {
                    name = "org.jooq.codegen.KotlinGenerator"

                    database.apply {
                        inputSchema = "PUBLIC"
                        includes = ".*"
                    }
                    target.apply {
                        packageName = "org.icpclive.balloons.db"
                        directory = "./build/jooq"
                    }
                }
            }
        }
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}