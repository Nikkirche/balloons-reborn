package org.icpclive.balloons.event

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.icpclive.cds.api.ContestInfo
import org.icpclive.cds.api.RunInfo

class EventStream {
    private val contest = MutableStateFlow(Contest("Loading", mapOf()))

    private val sink = MutableStateFlow<Pair<State, Event>>(State(listOf(), listOf()) to Reload)
    val stream = sink.asStateFlow()

    /**
     * @return `true` if command succeeded, `false` otherwise (in case of concurrent modification, etc.)
     */
    fun processCommand(
        command: Command,
        userId: Long,
    ): Boolean {
        return true
    }

    // This can be written in non-concurrent fashion.
    fun processContestInfo(contestInfo: ContestInfo) {
        val newContestInfo = Contest(contestInfo)
        if (newContestInfo != contest.value) {
            contest.update { newContestInfo }

            // Here something in team info may change. So we rewrite the whole history.
            sink.update { (state, _) ->
                val newState =
                    state.copy(
                        balloons =
                            state.balloons.map { balloon ->
                                balloon.copy(team = newContestInfo.getTeam(balloon.team.id))
                            },
                    )
                newState with Reload
            }
        }

        val problems = contestInfo.problems.map { (_, value) -> Problem(value) }.sortedBy { it.alias }
        if (problems != getState().problems) {
            updateSink(ProblemsUpdated(problems))
        }
    }

    // This can be written in non-concurrent fashion.
    fun processRun(runInfo: RunInfo) {
    }

    private fun getState() = sink.value.first

    private fun updateSink(event: Event) = sink.update { (state, _) -> state with event }

    /**
     * Use this as the last operator in [MutableStateFlow.update].
     *
     * It should be THE ONLY way to update the state. Each event should be applied in its own `update`.
     * The exception is [Reload] event that sends new state to connected clients. In that case, make
     * all updates inside [MutableStateFlow.update] and finish with `state with Reload`.
     */
    private infix fun State.with(event: Event) = event.applyTo(this) to event
}
