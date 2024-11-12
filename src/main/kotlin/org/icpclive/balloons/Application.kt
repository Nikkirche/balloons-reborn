package org.icpclive.balloons

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.groups.provideDelegate
import com.github.ajalt.clikt.parameters.options.defaultLazy
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.path
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.http.content.react
import io.ktor.server.http.content.singlePageApplication
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
import org.icpclive.balloons.db.DatabaseConfig
import org.icpclive.balloons.db.databaseModule
import org.icpclive.balloons.event.contestController
import org.icpclive.balloons.event.eventModule
import org.icpclive.balloons.event.launchCDSFetcher
import org.icpclive.cds.cli.CdsCommandLineOptions
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import kotlin.io.path.inputStream

object Application : CliktCommand("run") {
    override val printHelpOnEmptyArgs = true

    private val cdsSettings by CdsCommandLineOptions()

    private val databaseConfig: DatabaseConfig by requireObject()

    private val balloonConfigFile by option("--balloon-config", help = "Balloon utility config")
        .path(canBeDir = false, canBeSymlink = false, mustExist = true, mustBeReadable = true)
        .defaultLazy("<config-directory>/balloons.json") { cdsSettings.configDirectory.resolve("balloons.json") }

    @OptIn(ExperimentalSerializationApi::class)
    private val balloonConfig: BalloonConfig by lazy {
        balloonConfigFile.inputStream().buffered().use { Json { allowComments = true }.decodeFromStream(it) }
    }

    override fun run() {
        embeddedServer(Netty, port = balloonConfig.port) {
            install(Koin) {
                slf4jLogger()
                modules(
                    databaseModule(databaseConfig),
                    eventModule(cdsSettings),
                )
            }

            install(ContentNegotiation) { json() }

            install(WebSockets)

            installJwt(balloonConfig)

            launchCDSFetcher()

            routing {
                singlePageApplication {
                    react("frontend")
                    useResources = true
                }
                adminController()
                authController(balloonConfig)
                contestController(balloonConfig)
            }
        }.start(wait = true)
    }
}
