package org.icpclive.balloons

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.path
import org.icpclive.balloons.db.DatabaseConfig
import org.icpclive.balloons.tools.H2Shell
import org.icpclive.balloons.tools.Volunteer
import kotlin.io.path.Path

object Cli : CliktCommand("balloons") {
    init {
        subcommands(Application, H2Shell, Volunteer)
    }

    private val databaseFile by option(
        "-d",
        "--database-file",
        help = "Database location, see http://www.h2database.com/html/features.html#database_file_layout for details",
    )
        .path()
        .default(Path("./h2"))

    override fun run() {
        currentContext.findOrSetObject { DatabaseConfig(databaseFile) }
    }
}

fun main(args: Array<String>) {
    Cli.main(args)
}
