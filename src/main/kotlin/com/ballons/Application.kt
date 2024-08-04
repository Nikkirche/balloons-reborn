package com.balloons

import com.balloons.plugins.*
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.groups.provideDelegate
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.*
import org.icpclive.cds.cli.CdsCommandLineOptions

class FetcherCommand() : CliktCommand() {
    private val settings by CdsCommandLineOptions()
    override fun run() {
        CoroutineScope(Job()).launch {
            CDSFetcher(settings).run(this)
        }

    }
}

fun main(args: Array<String>) {
    FetcherCommand().main(args)
    embeddedServer(Netty, port = 8081, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureRouting()
}