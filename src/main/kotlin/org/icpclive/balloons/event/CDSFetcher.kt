package org.icpclive.balloons.event

import io.ktor.server.application.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.launch
import org.icpclive.cds.InfoUpdate
import org.icpclive.cds.RunUpdate
import org.icpclive.cds.cli.CdsCommandLineOptions
import org.koin.ktor.ext.inject

class CDSFetcher(
    private val eventStream: EventStream,
    settings: CdsCommandLineOptions,
) {
    private val cds = settings.toFlow()

    suspend fun run() {
        cds.buffer(capacity = UNLIMITED).collect { event ->
            when (event) {
                is RunUpdate -> eventStream.processRun(event.newInfo)
                is InfoUpdate -> eventStream.processContestInfo(event.newInfo)
                else -> {
                    // Unsupported command
                }
            }
        }
    }
}

fun Application.launchCDSFetcher() {
    val cdsFetcher: CDSFetcher by inject()

    CoroutineScope(Job()).launch {
        cdsFetcher.run()
    }
}
