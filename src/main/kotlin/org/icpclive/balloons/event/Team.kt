package org.icpclive.balloons.event

import kotlinx.serialization.Serializable
import org.icpclive.cds.api.TeamInfo

@Serializable
data class Team(
    val id: String,
    val displayName: String,
    val fullName: String,
    val place: String?,
    val hall: String?,
) {
    constructor(teamInfo: TeamInfo) : this(
        id = teamInfo.id.value,
        displayName = teamInfo.displayName,
        fullName = teamInfo.fullName,
        place = teamInfo.customFields["comp"],
        hall = teamInfo.customFields["hall"],
    )
}
