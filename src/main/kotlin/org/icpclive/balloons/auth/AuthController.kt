package org.icpclive.balloons.auth

import at.favre.lib.crypto.bcrypt.BCrypt
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import kotlinx.datetime.Clock
import kotlinx.datetime.toJavaInstant
import kotlinx.serialization.Serializable
import org.icpclive.balloons.BalloonConfig
import org.icpclive.balloons.db.VolunteerRepository
import org.koin.ktor.ext.inject
import kotlin.time.Duration.Companion.days

private const val BCRYPT_COST = 12

@Serializable
data class Credentials(
    val login: String,
    val password: String,
)

fun Route.authController(balloonConfig: BalloonConfig) {
    val bcryptHasher = BCrypt.withDefaults()
    val bcryptVerifier = BCrypt.verifyer()
    val volunteerRepository: VolunteerRepository by inject()

    val getJWTToken = { volunteerId: Long ->
        JWT.create()
            .withSubject(volunteerId.toString())
            .withExpiresAt(Clock.System.now().plus(365.days).toJavaInstant())
            .sign(Algorithm.HMAC256(balloonConfig.secretKey))
    }

    post("/api/login") {
        val credentials = call.receive<Credentials>()
        val volunteer = volunteerRepository.getByLogin(credentials.login)

        if (volunteer == null) {
            call.respond(HttpStatusCode.Forbidden)
            return@post
        }

        if (!bcryptVerifier.verify(credentials.password.toCharArray(), volunteer.passwordHash).verified) {
            call.respond(HttpStatusCode.Forbidden)
            return@post
        }

        call.respond(mapOf("token" to getJWTToken(volunteer.id!!)))
    }

    authenticate(optional = true) {
        post("/api/register") {
            val principal = call.principal<VolunteerPrincipal>()

            if (principal == null && !balloonConfig.allowPublicRegistration) {
                call.respond(HttpStatusCode.Forbidden)
                return@post
            }

            if (principal != null && principal.volunteer.canManage != true) {
                call.respond(HttpStatusCode.Forbidden)
                return@post
            }

            val credentials = call.receive<Credentials>()

            val newVolunteer =
                volunteerRepository.register(
                    credentials.login,
                    bcryptHasher.hashToString(BCRYPT_COST, credentials.password.toCharArray()),
                    // If admin is registering user, access is given by default
                    canAccess = principal != null,
                )

            if (newVolunteer == null) {
                call.respond(HttpStatusCode.Conflict, "User with the same login exists")
                return@post
            }

            if (principal == null) {
                // User is self registering, no need to authenticate again
                call.respond(mapOf("token" to getJWTToken(newVolunteer.id!!)))
            } else {
                call.respond(HttpStatusCode.Created)
            }
        }
    }
}
