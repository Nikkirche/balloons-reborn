package com.ballons

import com.balloons.CDSFetcher
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.groups.provideDelegate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.icpclive.cds.cli.CdsCommandLineOptions

object Config : CliktCommand(printHelpOnEmptyArgs = true) {
    val cdsSettings by CdsCommandLineOptions()
    override fun run() {
        CoroutineScope(Job()).launch {
            CDSFetcher(cdsSettings).run(this)
        }
    }
}

val config: Config get() = Config
