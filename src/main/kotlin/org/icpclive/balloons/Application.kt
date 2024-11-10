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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.icpclive.balloons.event.EventStream
import org.icpclive.cds.cli.CdsCommandLineOptions
import java.io.InputStream

@Serializable
class DbOptions(val user: String, val password: String) {
    @OptIn(ExperimentalSerializationApi::class)
    companion object {
        private val json = Json {
            allowComments = true
            allowTrailingComma = true
        }

        fun fromInputStream(input: InputStream): DbOptions =
            input.use { json.decodeFromStream<DbOptions>(it) }
    }
}

object Application : CliktCommand(printHelpOnEmptyArgs = true) {
    val cdsSettings by CdsCommandLineOptions()
    val dbConfig by option("--database-file", help = "Database config file")
        .path(mustExist = true, canBeFile = true, canBeDir = false)
        .defaultLazy("<config-directory>/db.json") { cdsSettings.configDirectory.resolve("db.json") }

    override fun run() {
        // Buy me a book on how to do dependency injection
        val eventStream = EventStream()
        val cdsFetcher = CDSFetcher(eventStream, cdsSettings)

        CoroutineScope(Job()).launch {
            cdsFetcher.run()
        }

        embeddedServer(Netty, port = 8001) {
            install(WebSockets)
            routing {
                balloonWebsocket(eventStream)
            }
        }
            .start(wait = true)
    }
}

fun main(args: Array<String>) {
    Application.main(args)
}