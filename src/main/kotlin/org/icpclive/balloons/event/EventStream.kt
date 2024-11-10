package org.icpclive.balloons.event

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.icpclive.cds.api.ContestInfo
import org.icpclive.cds.api.RunInfo

class EventStream {
    private val sink = MutableStateFlow<Pair<State, Event>>(State() to Event())
    val stream = sink.asStateFlow()

    /**
     * @return true if command succeeded, false otherwise (in case of concurrent modification, etc.)
     */
    fun processCommand(command: Command, userId: Long): Boolean {
        return true
    }

    fun processRun(runInfo: RunInfo) {

    }

    fun processContestInfo(contestInfo: ContestInfo) {

    }
}