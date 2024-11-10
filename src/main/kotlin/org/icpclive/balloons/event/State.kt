package org.icpclive.balloons.event

import kotlinx.serialization.Serializable

@Serializable
data class State(
    val problems: List<Problem>,
    val balloons: List<Balloon>
)