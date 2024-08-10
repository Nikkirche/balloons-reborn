package com.balloons

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

@Serializable
@Suppress("unused")
class ActionResponse<T>(
    val status: String,
    val response: T?
)
class ApiActionException(message: String, cause: Throwable? = null) : Exception(message, cause)

suspend inline fun <T> ApplicationCall.adminApiAction(
    responseSerializer: KSerializer<T>,
    block: ApplicationCall.() -> T
) = try {
    //todo add check about being admin
    val result = block()
    respondText(contentType = ContentType.Application.Json) {
        Json.encodeToString(
            ActionResponse.serializer(responseSerializer),
            ActionResponse("ok", result.takeUnless { it is Unit })
        )
    }
} catch (e: ApiActionException) {
    respond(HttpStatusCode.BadRequest, mapOf("status" to "error", "message" to e.message))
}

suspend inline fun <reified T> ApplicationCall.adminApiAction(block: ApplicationCall.() -> T) =
    adminApiAction(serializer(), block)