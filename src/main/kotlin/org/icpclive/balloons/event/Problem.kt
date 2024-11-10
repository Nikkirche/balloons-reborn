package org.icpclive.balloons.event

import kotlinx.serialization.Serializable

@Serializable
data class Problem(
    val id: String,
    val alias: String,
    val name: String,
    val color: String
)