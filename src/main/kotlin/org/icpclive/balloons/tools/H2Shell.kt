package org.icpclive.balloons.tools

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.requireObject
import org.icpclive.balloons.db.DatabaseConfig

object H2Shell : CliktCommand("h2shell") {
    override fun help(context: Context) = "Open database shell"

    private val databaseConfig: DatabaseConfig by requireObject()

    override fun run() {
        org.h2.tools.Shell.main("-url", databaseConfig.toJdbcUrl())
    }
}
