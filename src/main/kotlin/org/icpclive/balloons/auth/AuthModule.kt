package org.icpclive.balloons.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.JWTVerifier
import org.icpclive.balloons.BalloonConfig
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun authModule(balloonConfig: BalloonConfig) =
    module {
        single<JWTVerifier> { JWT.require(Algorithm.HMAC256(balloonConfig.secretKey)).build() }
        singleOf(::CredentialValidator)
        singleOf(::WebSocketAuthenticator)
    }