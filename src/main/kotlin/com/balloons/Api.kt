package com.balloons


import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow

object Submissions : IntIdTable("balloons") {
    val eventId = integer("event_id")

    val problemId = integer("problem_id")
    val teamId = integer("team_id")
    val volunteerId = varchar("volunteer_id", 255).default("")
    val state = integer("state").default(0)

    //  todo what is it....
    val timeLocal = double("time_local").default(0.0)
    val timeCreated = integer("time_created")
    val timeTaken = integer("time_taken").default(0)
    val timeDelivered = integer("time_delivered").default(0)

    // variable to avoid duplicates by selecting
    val runId = varchar("run_id", 255)
    val eventState = index("event_state", false, eventId, state)
    val eventVolunteer = index("event_volunteer", false, eventId, volunteerId, state)
    val eventProblem = index("event_problem", false, eventId, runId)
    val eventTeam = index("event_team", false, eventId, teamId)

}

object Events : IntIdTable("events") {
    val name = varchar("name", 255)
    val state = integer("state").default(1)
    val hasCustomMapping = bool("has_custom_mapping").default(false)
}

object Teams : IntIdTable("teams") {
    val name = varchar("name", 255)
    val state = integer("state").default(1)
    val eventId = integer("event_id")
    val longName = varchar("long_name", 255)
    val place = varchar("place", 255).nullable()
    val hall = varchar("hall", 255).nullable()
}

data class Team(
    val id: Int,
    val name: String,
    val eventId: Int,
    val longName: String,
    val place: String?,
    val hall: String?
) {
    companion object {
        fun fromRow(resultRow: ResultRow) = Team(
            id = resultRow[Teams.id].value,
            name = resultRow[Teams.name],
            eventId = resultRow[Teams.eventId],
            longName = resultRow[Teams.longName],
            place = resultRow[Teams.place],
            hall = resultRow[Teams.hall]
        )

        fun mappingFromRow(resultRow: ResultRow) = Mapping(
            team = resultRow[Teams.name],
            value = MappingValue(hall = resultRow[Teams.hall].toString(), place = resultRow[Teams.place].toString())
        )
    }
}

object Problems : IntIdTable("problems") {
    val letter = varchar("letter", 255)
    val name = varchar("name", 255)
    val color = varchar("color", 255).default("")
    val eventId = integer("event_id")
}

data class Problem(val id: Int, val name: String, val eventId: Int) {
    companion object {
        fun fromRow(resultRow: ResultRow) = Problem(
            id = resultRow[Problems.id].value,
            name = resultRow[Problems.name],
            eventId = resultRow[Problems.eventId]
        )
    }
}

object Volunteers : IntIdTable("volunteers") {
    val login = varchar("login", 255)
    val name = varchar("name", 255)
    val password = varchar("password", 255)
    val access = integer("access")
}