package org.icpclive.balloons.event

import kotlinx.serialization.Serializable
import org.icpclive.cds.api.ProblemInfo

@Serializable
data class Problem(
    val id: String,
    val alias: String,
    val name: String,
    val color: String?
) {
    constructor(problemInfo: ProblemInfo) : this(
        id = problemInfo.id.value,
        alias = problemInfo.displayName,
        name = problemInfo.fullName,
        color = problemInfo.color?.value
    )
}