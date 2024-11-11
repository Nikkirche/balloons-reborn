package org.icpclive.balloons.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.response.respond
import org.icpclive.balloons.BalloonConfig
import org.icpclive.balloons.db.VolunteerRepository
import org.icpclive.cds.util.getLogger
import org.koin.ktor.ext.inject

fun Application.installJwt(balloonConfig: BalloonConfig) {
    val volunteerRepository: VolunteerRepository by inject()

    install(Authentication) {
        jwt {
            realm = "balloons"
            verifier(JWT.require(Algorithm.HMAC256(balloonConfig.secretKey)).build())
            validate { credential ->
                credential.payload.let {
                    logger.info { "You are $it" }
                    val volunteerId = it.subject?.toLongOrNull() ?: return@let null
                    logger.info { "Claim is $it" }
                    val volunteer = volunteerRepository.getById(volunteerId) ?: return@let null
                    logger.info { "Volunteer is $it" }

                    VolunteerPrincipal(volunteer, it)
                }
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized)
            }
        }
    }
}

private val logger by getLogger()
