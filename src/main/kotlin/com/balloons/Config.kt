package com.balloons

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.groups.provideDelegate
import com.github.ajalt.clikt.parameters.options.defaultLazy
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.path
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.icpclive.cds.cli.CdsCommandLineOptions
import java.io.InputStream

@Serializable
class DbOptions(public val user: String, public val password: String) {
    @OptIn(ExperimentalSerializationApi::class)
    companion object {
        private val json = Json {
            allowComments = true
            allowTrailingComma = true
        }

        fun fromInputStream(input: InputStream): DbOptions =
            json.decodeFromStream<DbOptions>(input)
    }
}

object Config : CliktCommand(printHelpOnEmptyArgs = true) {
    val cdsSettings by CdsCommandLineOptions()
    val dbConfig by option("--database-file", help = "Database config file")
        .path(canBeDir = false, mustExist = true, canBeFile = true)
        .defaultLazy("configDirectory/db.json") { cdsSettings.configDirectory.resolve("db.json") }

    override fun run() {
        CoroutineScope(Job()).launch {
            CDSFetcher(cdsSettings).run(this)
        }
    }
}

val config: Config get() = Config
