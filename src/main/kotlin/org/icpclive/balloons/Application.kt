package org.icpclive.balloons

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.groups.provideDelegate
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.defaultLazy
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.path
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.icpclive.balloons.admin.adminController
import org.icpclive.balloons.auth.authController
import org.icpclive.balloons.auth.installJwt
import org.icpclive.balloons.db.databaseModule
import org.icpclive.balloons.event.eventModule
import org.icpclive.balloons.event.launchCDSFetcher
import org.icpclive.cds.cli.CdsCommandLineOptions
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.koin.ktor.plugin.KoinApplicationStopped
import org.koin.logger.slf4jLogger
import java.sql.Connection
import kotlin.io.path.Path
import kotlin.io.path.inputStream

object Application : CliktCommand("balloons") {
    override val printHelpOnEmptyArgs: Boolean
        get() = true

    private val cdsSettings by CdsCommandLineOptions()
    private val databaseFile by option("--database-file", help = "Database location")
        .path(canBeDir = false, canBeSymlink = false)
        .default(Path("./db.h2"))
    private val balloonConfigFile by option("--balloon-config", help = "Balloon utility config")
        .path(canBeDir = false, canBeSymlink = false, mustExist = true, mustBeReadable = true)
        .defaultLazy("<config-directory>/balloons.json") { cdsSettings.configDirectory.resolve("balloons.json") }

    @OptIn(ExperimentalSerializationApi::class)
    private val balloonConfig: BalloonConfig by lazy {
        balloonConfigFile.inputStream().buffered().use { Json.decodeFromStream(it) }
    }

    override fun run() {
        embeddedServer(Netty, port = 8001) {
            install(Koin) {
                slf4jLogger()
                modules(
                    databaseModule(databaseFile),
                    eventModule(cdsSettings),
                )
            }

            environment.monitor.subscribe(KoinApplicationStopped) {
                // Ensure we close connection to database when stopping the application.
                val database: Connection by inject()
                runCatching { database.close() }
            }

            install(ContentNegotiation) { json() }

            install(WebSockets)

            installJwt(balloonConfig)

            launchCDSFetcher()

            routing {
                adminController()
                authController(balloonConfig)
                balloonWebsocket()
            }
        }.start(wait = true)
    }
}

fun main(args: Array<String>) {
    Application.main(args)
}
