package com.balloons

import app.softwork.serialization.csv.CSVFormat
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer

@Serializable
data class MappingValue(val place: String, val hall: String)

@Serializable
data class Mapping(val team: String, val value: MappingValue) {
    @OptIn(ExperimentalSerializationApi::class)
    companion object {
        fun fromString(string: String): List<Mapping> {
            return CSVFormat.decodeFromString(ListSerializer(serializer()), string)
        }

        fun fromMapping(mapping: List<Mapping>): String {
            return CSVFormat.encodeToString(ListSerializer(serializer()), mapping)
        }
    }
}