package org.icpclive.balloons.event

import kotlinx.serialization.Serializable
import org.icpclive.cds.api.ContestInfo

@Serializable
data class Contest(
    val name: String,
    val teams: Map<String, Team>,
) {
    constructor(contestInfo: ContestInfo) : this(
        name = contestInfo.name,
        teams =
            contestInfo.teams
                .mapKeys { (key, _) -> key.value }
                .mapValues { (_, value) -> Team(value) },
    )

    fun getTeam(id: String) = teams[id] ?: Team(id, "??", "???", null, null)
}
