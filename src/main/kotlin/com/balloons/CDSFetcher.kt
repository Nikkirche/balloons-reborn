package com.balloons

import com.ballons.Storage
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.icpclive.cds.InfoUpdate
import org.icpclive.cds.RunUpdate
import org.icpclive.cds.api.*
import org.icpclive.cds.cli.CdsCommandLineOptions
import org.icpclive.cds.util.getLogger

class CDSFetcher(settings: CdsCommandLineOptions) {
    val storage = Storage()
    //todo make logging option from cmd
    private val cds = settings.toFlow()
    private val contestInfo = CompletableDeferred<StateFlow<ContestInfo>>()
    fun run(scope: CoroutineScope) {
        val loaded = cds.shareIn(scope, SharingStarted.Eagerly, Int.MAX_VALUE)
        val runs = loaded.filterIsInstance<RunUpdate>().map { it.newInfo }
        val event = loaded.filterIsInstance<InfoUpdate>().map { it.newInfo }
        scope.launch {
            contestInfo.complete(loaded.filterIsInstance<InfoUpdate>().map { it.newInfo }.stateIn(scope))
        }
        logger.info { "starting runs processing for contest  ..." }
        scope.launch {
            val info = contestInfo.await().value
            val eventId = storage.createEvent(info)
            val problems = storage.getProblemsFromEvent(eventId)
            val teams = storage.getTeamsFromEvent(eventId)
            scope.launch {
                event.collect {
                    for ((k, v) in it.problems.entries) {
                        if (k.value !in problems) {
                            val i = storage.addProblem(k.value, v, eventId)
                            problems[k.value] = i
                        }
                    }
                    for ((k, v) in it.teams.entries) {
                        if (k.value !in teams) {
                            val i = storage.addTeam(k.value, v, eventId)
                            teams[k.value] = i
                        }
                    }
                }
            }
            scope.launch {
                println("runs processing stated for ${info.currentContestTime}")
                val submissions = storage.getRunsFromEvent(eventId)
                runs.filter { it.result.isSolved() }.collect {
                    if (!submissions.contains(it.id.value)) {
                        storage.createSubmission(eventId, problems[it.problemId.value]!!,teams[it.teamId.value]!!,it)
                        submissions.add(it.id.value)
                    }
                }
            }
        }
    }

    private fun RunResult.isSolved(): Boolean = when (this) {
        is RunResult.ICPC -> verdict.isAccepted
        is RunResult.IOI -> false
        is RunResult.InProgress -> false
    }
    private companion object {
        val logger by getLogger()
    }
}