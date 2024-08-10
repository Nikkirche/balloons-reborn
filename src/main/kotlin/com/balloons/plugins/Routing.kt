package com.balloons.plugins

import com.ballons.Storage
import com.balloons.ApiActionException
import com.balloons.Mapping
import com.balloons.adminApiAction
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.Resources
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.post
import kotlinx.serialization.SerializationException


@Resource("{id}")
class EventId(val id: Int)


fun Application.configureRouting() {
    install(Resources)
    val storage = Storage
    routing {
        get("/") {

            call.respondText("Hello World!")
        }
        route("/api/mapping") {
            get<EventId> { eventId ->
                val mapping = Mapping.fromMapping(storage.getMapping(eventId.id))
                //todo looks like this should be send by respondFile
                call.respondText(mapping)
            }
            post<EventId> { eventId ->
                call.adminApiAction {
                    val text = call.receiveText()
                    try {
                        val mapRequest = Mapping.fromString(text).associate { Pair(it.team, it.value) }
                        val mapCurr = storage.getMapping(eventId.id).associate { Pair(it.team, it.value) }
                        val diffTeams = mapRequest.keys.subtract(mapCurr.keys)
                        if(diffTeams.isNotEmpty()){
                           throw ApiActionException("Couldn't find following teams in database: ${diffTeams.joinToString()}")
                        }
                        val mapWrite = mutableListOf<Mapping>()
                        mapRequest.entries.forEach { (team, value) ->
                            if(value == mapCurr[team]){
                                mapWrite.addLast(Mapping(team, value))
                            }
                        }
                        storage.writeMapping(eventId.id, mapWrite)
                    } catch (e: SerializationException) {
                        throw ApiActionException("Couldn't serialize mapping file: ${e.message}", e)
                    }
                }
            }
        }
    }
}
