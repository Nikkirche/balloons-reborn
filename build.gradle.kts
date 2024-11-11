import org.jooq.meta.jaxb.Logging

plugins {
    java
    alias(libs.plugins.jooq)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ktor)
}

group = "org.icpclive.balloons"
version = "0.0.1"

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("org.icpclive.balloons.CliKt")

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
    jooqGenerator(libs.h2)

    implementation(libs.bundles.koin)
    implementation(libs.bundles.ktor)
    implementation(libs.logback)
    implementation(libs.live.cds)
    implementation(libs.kotlin.serialization.json)
    implementation(libs.h2)
    implementation(libs.jooq.kotlin)
    implementation(libs.bcrypt)

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
                    generate.apply {
                        isKotlinNotNullPojoAttributes = true
                        isKotlinNotNullRecordAttributes = true
                        isKotlinNotNullInterfaceAttributes = true
                        isKotlinDefaultedNullablePojoAttributes = false
                        isKotlinDefaultedNullableRecordAttributes = false
                    }
                }
            }
        }
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}