package org.icpclive.balloons

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.groups.provideDelegate
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.path
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respond
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import org.icpclive.balloons.db.H2Connection
import org.icpclive.balloons.db.databaseModule
import org.icpclive.balloons.event.eventModule
import org.icpclive.balloons.event.launchCDSFetcher
import org.icpclive.cds.cli.CdsCommandLineOptions
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.koin.ktor.plugin.KoinApplicationStopped
import org.koin.logger.slf4jLogger
import kotlin.io.path.Path

object Application : CliktCommand("balloons") {
    override val printHelpOnEmptyArgs: Boolean
        get() = true

    private val cdsSettings by CdsCommandLineOptions()
    private val databaseFile by option("--database-file", help = "Database location")
        .path(canBeDir = false, canBeSymlink = false)
        .default(Path("./db.h2"))

    override fun run() {
        embeddedServer(Netty, port = 8001) {
            install(Koin) {
                slf4jLogger()
                modules(
                    databaseModule(databaseFile),
                    eventModule(cdsSettings)
                )
            }

            environment.monitor.subscribe(KoinApplicationStopped) {
                val database: H2Connection by inject()
                runCatching { database.close() }
            }

            install(WebSockets)

            install(Authentication) {
                jwt {
                    realm = "balloons"
                    verifier(
                        JWT.require(Algorithm.HMAC256("aboba"))
                            .build()
                    )
                    validate { credential ->
                        credential.payload
                            .takeIf { it.getClaim("volunteerId").asInt() != null }
                            ?.let(::JWTPrincipal)
                    }
                    challenge { _, _ -> call.respond(HttpStatusCode.Unauthorized) }
                }
            }

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
