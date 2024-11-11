package org.icpclive.balloons.tools

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import org.icpclive.balloons.db.DatabaseConfig
import org.icpclive.balloons.db.VolunteerRepository
import org.icpclive.balloons.db.databaseModule
import org.icpclive.cds.util.getLogger
import org.koin.core.Koin
import org.koin.core.context.startKoin
import kotlin.system.exitProcess

object Volunteer : CliktCommand("volunteer") {
    override fun help(context: Context) = "Manage volunteers"

    init {
        subcommands(CreateVolunteer, UpdateVolunteer)
    }

    private val databaseConfig: DatabaseConfig by requireObject()

    override fun run() {
        currentContext.findOrSetObject {
            startKoin { modules(databaseModule(databaseConfig)) }.koin
        }
    }
}

object CreateVolunteer : CliktCommand("create") {
    override val printHelpOnEmptyArgs = true

    private val koin: Koin by requireObject()

    private val admin by option().flag().help("Make this volunteer an admin")
    private val login by argument()
    private val password by argument()

    override fun run() {
        val volunteerRepository = koin.get<VolunteerRepository>()

        if (volunteerRepository.register(login, password, canAccess = true, canManage = admin) != null) {
            logger.info { "Volunteer $login created" }
        } else {
            logger.error { "Volunteer not created: probably it already exists." }
            exitProcess(1)
        }
    }
}

object UpdateVolunteer : CliktCommand("update") {
    override val printHelpOnEmptyArgs = true

    private val koin: Koin by requireObject()

    private val login by argument()
    private val makeAdmin by option().flag().help("Make this volunteer an admin")
    private val newPassword by option().help("Set new password")

    override fun run() {
        val volunteerRepository = koin.get<VolunteerRepository>()

        if (newPassword == null && !makeAdmin) {
            logger.error { "Nothing to update" }
            exitProcess(1)
        }

        val volunteer = volunteerRepository.getByLogin(login)

        if (volunteer == null) {
            logger.error { "Volunteer not found" }
            exitProcess(1)
        }

        newPassword?.let {
            volunteerRepository.setPassword(volunteer.id!!, it)
            logger.info { "Password for volunteer $login updated" }
        }
        if (makeAdmin) {
            volunteerRepository.updateFlags(volunteer.id!!, canManage = true)
            logger.info { "Volunteer $login is admin now" }
        }
    }
}

private val logger by getLogger()
