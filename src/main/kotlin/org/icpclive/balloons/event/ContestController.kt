package org.icpclive.balloons.event

import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.JWTVerifier
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTCredential
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.coroutines.channels.consume
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.icpclive.balloons.BalloonConfig
import org.icpclive.balloons.auth.CredentialValidator
import org.icpclive.balloons.auth.VolunteerPrincipal
import org.icpclive.balloons.auth.WebSocketAuthenticator
import org.icpclive.cds.util.getLogger
import org.koin.ktor.ext.inject

@Serializable
data class ContestInfo(
    val contestName: String,
    val canRegister: Boolean? = null,
    val login: String? = null,
    val canAccess: Boolean? = null,
    val canManage: Boolean? = null,
)

fun Route.contestController(balloonConfig: BalloonConfig) {
    val eventStream: EventStream by inject()
    val webSocketAuthenticator: WebSocketAuthenticator by inject()

    authenticate(optional = true) {
        get("/api/info") {
            val principal = call.principal<VolunteerPrincipal>()

            val contestName = eventStream.contest.value.name

            if (principal == null) {
                call.respond(ContestInfo(contestName, canRegister = balloonConfig.allowPublicRegistration))
            } else {
                val volunteer = principal.volunteer
                call.respond(
                    ContestInfo(contestName, login = volunteer.login, canAccess = volunteer.canAccess, canManage = volunteer.canManage),
                )
            }
        }
    }

    webSocket("/api/balloons") {
        val principal = webSocketAuthenticator.authenticate(this)

        if (principal?.volunteer?.canAccess != true) {
            // ktor gives us `webSocketRaw` or `webSocket`. First requires manual processing of all frames (including pings),
            // second doesn't allow us to return HTTP response, so here we go.
            send("""{"error": "access denied"}""")
            return@webSocket
        }

        val outgoingStream =
            launch {
                var expectState = true

                eventStream.stream.collect { (state, event) ->
                    if (expectState || event == Reload) {
                        expectState = false
                        send(jsonSerializer.encodeToString(state))
                    } else {
                        send(jsonSerializer.encodeToString(event))
                    }
                }
            }

        runCatching {
            incoming.consumeEach { frame ->
                if (frame !is Frame.Text) {
                    return@consumeEach
                }

                val command = jsonSerializer.decodeFromString<Command>(frame.readText())
                if (!eventStream.processCommand(command, volunteerId = principal.volunteer.id!!)) {
                    send("""{"error": "command failed"}""")
                }
            }
        }.onFailure { exception ->
            logger.warning { "WebSocket exception: ${exception.localizedMessage}" }
        }.also {
            outgoingStream.cancel()
        }
    }
}

private val logger by getLogger()
private val jsonSerializer = Json { encodeDefaults = true }