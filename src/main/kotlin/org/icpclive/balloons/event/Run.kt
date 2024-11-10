package org.icpclive.balloons.event

import org.icpclive.cds.api.RunInfo
import kotlin.time.Duration

data class Run(
    val runId: String,
    val teamId: String,
    val problemId: String,
    val time: Duration
) : Comparable<Run> {
    constructor(runInfo: RunInfo) : this(
        runId = runInfo.id.value,
        teamId = runInfo.teamId.value,
        problemId = runInfo.problemId.value,
        time = runInfo.time
    )

    override fun compareTo(other: Run): Int = compareValuesBy(this, other, { it.time }, { it.runId })
}