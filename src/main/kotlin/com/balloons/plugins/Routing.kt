package com.balloons.plugins

import com.ballons.Storage
import com.balloons.ApiActionException
import com.balloons.Mapping
import com.balloons.adminApiAction
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.post
import kotlinx.serialization.SerializationException
import org.icpclive.cds.util.getLogger

@Resource("api/mapping")
class MappingApi {
    @Resource("{id}")
    class EventId(val parent: MappingApi = MappingApi(), val id: Int)
}

val logger by getLogger()

fun Application.configureRouting() {
    val storage = Storage
    routing {
        get<MappingApi.EventId> { eventId ->
            val mapping = Mapping.fromMapping(storage.getMapping(eventId.id))
            call.respondText(contentType = ContentType.Text.CSV) { mapping }
        }
        post<MappingApi>{
            call.adminApiAction {
                var eventId: Int? = null
                var text: String? = null
                call.receiveMultipart().forEachPart { part ->
                    when (part) {
                        is PartData.FileItem -> {
                            text = part.streamProvider().bufferedReader().use { it.readText() }
                        }

                        is PartData.FormItem -> {
                            when (val arg = part.name) {
                                "eventId" -> {
                                    eventId = part.value.toInt()
                                }

                                else -> throw ApiActionException("Unexpected form part name - $arg")
                            }
                        }

                        else -> {
                            throw ApiActionException("Unexpected part data type ${part.javaClass.name}")
                        }
                    }
                    part.dispose()
                }
                if (eventId == null || text == null) {
                    throw ApiActionException("Required parts weren't' given");
                }
                try {
                    val mapRequest = Mapping.fromString(text!!).associate { Pair(it.team, it.value) }
                    val mapCurr = storage.getMapping(eventId!!).associate { Pair(it.team, it.value) }
                    val diffTeams = mapRequest.keys.subtract(mapCurr.keys)
                    if (diffTeams.isNotEmpty()) {
                        throw ApiActionException("Couldn't find following teams in database: ${diffTeams.joinToString()}")
                    }
                    val mapWrite = mutableListOf<Mapping>()
                    mapRequest.entries.forEach { (team, value) ->
                        if (value != mapCurr[team]) {
                            mapWrite.addLast(Mapping(team, value))
                        }
                    }
                    logger.info { "Updating ${mapWrite.size} mappings" }
                    storage.writeMapping(eventId!!, mapWrite)
                    mapWrite.size
                } catch (e: SerializationException) {
                    throw ApiActionException("Couldn't serialize mapping file: ${e.message}", e)
                }
            }
        }
    }
}
