package com.balloons.plugins

import com.balloons.Storage
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    val storage = Storage()
    routing {
        get("/") {

            call.respondText("Hello World!")
        }
        get("/events/{id}/mapping"){
            val eventId = call.parameters["id"]!!.toInt()
            call.respondText(storage.getTeams(eventId).joinToString { it.name })
        }
    }
}
