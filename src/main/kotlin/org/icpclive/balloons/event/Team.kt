package org.icpclive.balloons.event

import kotlinx.serialization.Serializable
import org.icpclive.cds.api.TeamInfo

@Serializable
data class Team(
    val id: String,
    val displayName: String,
    val fullName: String,
    val hall: String?,
) {
    constructor(teamInfo: TeamInfo) : this(
        id = teamInfo.id.value,
        displayName = teamInfo.displayName,
        fullName = teamInfo.fullName,
        hall = teamInfo.groups.map { it.value }.find { it.startsWith(HALL_GROUP) }?.removePrefix(HALL_GROUP),
    )

    companion object {
        private const val HALL_GROUP = "hall-"
    }
}
