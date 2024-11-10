package org.icpclive.balloons.event

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface Event {
    fun applyTo(state: State): State
}

@Serializable
@SerialName("reload")
data object Reload : Event {
    override fun applyTo(state: State) = state
}

@Serializable
@SerialName("problemsUpdated")
data class ProblemsUpdated(val problems: List<Problem>) : Event {
    override fun applyTo(state: State) = state.copy(problems = problems)
}

@Serializable
@SerialName("balloonUpdated")
data class BalloonUpdated(val balloon: Balloon) : Event {
    override fun applyTo(state: State) =
        state.copy(balloons = state.balloons.filter { it.runId != balloon.runId }.plus(balloon).sortedBy { it.time })
}

@Serializable
@SerialName("balloonDeleted")
data class BalloonDeleted(val runId: String) : Event {
    override fun applyTo(state: State) =
        state.copy(balloons = state.balloons.filter { it.runId != runId })
}