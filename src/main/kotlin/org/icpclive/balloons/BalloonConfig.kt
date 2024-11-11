package org.icpclive.balloons

import kotlinx.serialization.Serializable

@Serializable
data class BalloonConfig(
    val secretKey: String,
    val allowPublicRegistration: Boolean = true,
)
