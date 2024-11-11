package org.icpclive.balloons.admin

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import org.icpclive.balloons.auth.VolunteerPrincipal
import org.icpclive.balloons.db.VolunteerRepository
import org.koin.ktor.ext.inject

fun Route.adminController() {
    val volunteerRepository: VolunteerRepository by inject()

    authenticate {
        get("/api/volunteers") {
            val principal = call.principal<VolunteerPrincipal>()
            if (principal?.volunteer?.canManage != true) {
                call.respond(HttpStatusCode.Forbidden)
                return@get
            }

            val volunteers = volunteerRepository.all().map(::VolunteerView)

            call.respond(volunteers)
        }
    }
}
