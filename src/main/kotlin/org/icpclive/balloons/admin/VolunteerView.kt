package org.icpclive.balloons.admin

import kotlinx.serialization.Serializable
import org.icpclive.balloons.db.tables.records.VolunteerRecord

@Serializable
data class VolunteerView(
    val id: Long,
    val login: String,
    val canAccess: Boolean,
    val canManage: Boolean
) {
    constructor(record: VolunteerRecord) : this(
        record.id!!,
        record.login,
        record.canAccess!!,
        record.canManage!!
    )
}