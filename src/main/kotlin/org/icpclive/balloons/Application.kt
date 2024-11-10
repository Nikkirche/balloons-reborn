package org.icpclive.balloons

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.groups.provideDelegate
import com.github.ajalt.clikt.parameters.options.defaultLazy
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.path
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.icpclive.balloons.event.EventStream
import org.icpclive.cds.cli.CdsCommandLineOptions
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import java.io.InputStream

@Serializable
class DbOptions(val user: String, val password: String) {
    @OptIn(ExperimentalSerializationApi::class)
    companion object {
        private val json =
            Json {
                allowComments = true
                allowTrailingComma = true
            }

        fun fromInputStream(input: InputStream): DbOptions = input.use { json.decodeFromStream<DbOptions>(it) }
    }
}

object Application : CliktCommand(printHelpOnEmptyArgs = true) {
    val cdsSettings by CdsCommandLineOptions()
    val dbConfig by option("--database-file", help = "Database config file").path(mustExist = true, canBeFile = true, canBeDir = false)
        .defaultLazy("<config-directory>/db.json") { cdsSettings.configDirectory.resolve("db.json") }

    override fun run() {
        val app =
            module {
                single { EventStream() }
                single { CDSFetcher(get(), cdsSettings) }
            }

        embeddedServer(Netty, port = 8001) {
            install(Koin) {
                slf4jLogger()
                modules(app)
            }
            install(WebSockets)

            launchCDSFetcher()

            routing {
                balloonWebsocket()
            }
        }.start(wait = true)
    }
}

fun main(args: Array<String>) {
    Application.main(args)
}
