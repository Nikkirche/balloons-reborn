package com.ballons

import com.ballons.Submissions.runId
import org.icpclive.cds.api.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.time.DurationUnit

class Storage {
    private val connection = Database.connect(
        url = "jdbc:mariadb://localhost:3306/balloons",
        driver = "org.mariadb.jdbc.Driver",
        user = System.getenv("USER")!!,
        password = System.getenv("PASSWORD")!!
    )

    init {
        transaction(connection) {
            SchemaUtils.create(Submissions)
            SchemaUtils.create(Events)
            SchemaUtils.create(Problems)
            SchemaUtils.create(Teams)
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
                    it[state] = info.status.ordinal
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
                it[Problems.name] = problem.fullName
                it[Problems.eventId] = eventId
                it[Problems.letter] = id
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
        return transaction {
            return@transaction Teams.insertAndGetId {
                it[Teams.longName] = team.fullName
                it[Teams.eventId] = eventId
                it[Teams.name] = id
            }
        }.value
    }

    fun getTeams(eventId: Int): List<Team> {
        return transaction {
            return@transaction Teams.selectAll().where(Teams.eventId eq eventId).map { Team.fromRow(it) }
                .sortedBy { it.name }
        }
    }
}