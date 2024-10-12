package com.ballons

import com.balloons.*
import com.balloons.Submissions.runId
import org.icpclive.cds.api.ContestInfo
import org.icpclive.cds.api.ProblemInfo
import org.icpclive.cds.api.RunInfo
import org.icpclive.cds.api.TeamInfo
import org.icpclive.cds.util.getLogger
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.time.DurationUnit

object Storage {
    val connection: Database
    val logger by getLogger()

    init {
        val dbSettings = DbOptions.fromInputStream(Config.dbConfig.toFile().inputStream())
        connection = Database.connect(
            url = "jdbc:mariadb://localhost:3306/balloons",
            driver = "org.mariadb.jdbc.Driver",
            user = dbSettings.user,
            password = dbSettings.password
        )
        transaction(connection) {
            SchemaUtils.create(Submissions)
            SchemaUtils.create(Events)
            SchemaUtils.create(Problems)
            SchemaUtils.create(Teams)
            SchemaUtils.create(Volunteers)
        }
    }

    fun createSubmission(eid: Int, problemId: Int, team: Int, run: RunInfo) {
        transaction(connection) {
            Submissions.insert {
                it[eventId] = eid
                it[teamId] = team
                //todo
                it[Submissions.problemId] = problemId
                it[Submissions.runId] = run.problemId.value
                //todo replace with normal time?
                it[timeCreated] = run.time.toInt(DurationUnit.MINUTES)
                it[runId] = run.id.value
            }
        }
    }

    fun getRunsFromEvent(eid: Int): MutableSet<String> {
        return transaction(connection) {
            return@transaction Submissions.select(runId).where { Submissions.eventId eq eid }
                .map { it[runId] }.toMutableSet()
        }
    }

    fun createEvent(info: ContestInfo): Int {
        val k = transaction(connection) {
            val t = Events.select(Events.id).where { Events.name eq info.name }
            if (t.count() == 0.toLong()) {
                return@transaction Events.insertAndGetId {
                    it[name] = info.name
                    it[state] = 1
                }
            } else {
                return@transaction t.first()[Events.id]
            }
        }.value
        return k
    }

    fun getProblemsFromEvent(eventId: Int): MutableMap<String, Int> {
        val m = mutableMapOf<String, Int>()
        return transaction {
            return@transaction Problems.select(Problems.letter, Problems.id)
                .where(Problems.eventId eq eventId).associateTo(m) { it[Problems.letter] to it[Problems.id].value }
        }
    }

    fun addProblem(id: String, problem: ProblemInfo, eventId: Int): Int {
        return transaction {
            return@transaction Problems.insertAndGetId {
                it[name] = problem.fullName
                it[Problems.eventId] = eventId
                it[letter] = id
            }.value
        }
    }

    fun getTeamsFromEvent(eventId: Int): MutableMap<String, Int> {
        val m = mutableMapOf<String, Int>()
        return transaction {
            return@transaction Teams.select(Teams.name, Teams.id)
                .where(Teams.eventId eq eventId).associateTo(m) { it[Teams.name] to it[Teams.id].value }
        }
    }

    fun addTeam(id: String, team: TeamInfo, eventId: Int): Int {
        transaction { }
        val mapping = getTeamPlaceAndHall(team)
        return transaction {
            return@transaction Teams.insertAndGetId {
                it[longName] = team.fullName
                it[Teams.eventId] = eventId
                it[name] = id
                it[hall] = mapping.second
                it[place] = mapping.first
            }
        }.value
    }

    fun getTeams(eventId: Int): List<Team> {
        return transaction {
            return@transaction Teams.selectAll().where(Teams.eventId eq eventId).map { Team.fromRow(it) }
                .sortedBy { it.name }
        }
    }

    fun getMapping(eventId: Int): List<Mapping> {
        return transaction {
            return@transaction Teams.select(Teams.name, Teams.place, Teams.hall).where(Teams.eventId eq eventId)
                .map { Team.mappingFromRow(it) }
        }
    }

    fun writeMapping(eventId: Int, mapping: List<Mapping>) {
        transaction {
            for (m in mapping) {
                Teams.update({ (Teams.eventId eq eventId) and (Teams.name eq m.team) }) {
                    val value = m.value
                    it[Teams.hall] = value.hall.trim()
                    it[Teams.place] = value.place.trim()
                }
            }
        }
    }

    fun getTeamPlaceAndHall(teamInfo: TeamInfo): Pair<String?, String?> {
        var place = teamInfo.customFields["comp"]
        val hall = teamInfo.customFields["hall"]
        if (place == null) {
            //at least it is unique for the contest
            place = teamInfo.id.value
            logger.warning { "Couldn't get place for team with following id - ${teamInfo.id.value}, customFields are following - ${teamInfo.customFields}" }
        }
        if (hall == null) {
            logger.warning { "Couldn't get hall for following place - $place, customFields are following - ${teamInfo.customFields}" }
        }
        return Pair(place, hall)
    }
}