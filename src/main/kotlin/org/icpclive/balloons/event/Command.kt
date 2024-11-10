package org.icpclive.balloons.event

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface Command

@Serializable
@SerialName("takeBalloon")
data class TakeBalloon(val runId: String) : Command

@Serializable
@SerialName("dropBalloon")
data class DropBalloon(val runId: String) : Command

@Serializable
@SerialName("deliverBalloon")
data class DeliverBalloon(val runId: String) : Command