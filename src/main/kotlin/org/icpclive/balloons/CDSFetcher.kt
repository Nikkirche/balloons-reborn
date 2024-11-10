package org.icpclive.balloons

import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.buffer
import org.icpclive.balloons.event.EventStream
import org.icpclive.cds.InfoUpdate
import org.icpclive.cds.RunUpdate
import org.icpclive.cds.cli.CdsCommandLineOptions

class CDSFetcher(
    private val eventStream: EventStream,
    settings: CdsCommandLineOptions
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