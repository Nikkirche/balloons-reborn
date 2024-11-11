package org.icpclive.balloons.db

import org.icpclive.balloons.db.tables.records.VolunteerRecord
import org.icpclive.balloons.db.tables.references.VOLUNTEER
import org.jooq.DSLContext
import org.jooq.impl.DSL

class VolunteerRepository(private val jooq: DSLContext) {
    fun getById(id: Long) = jooq.fetchOne(VOLUNTEER, VOLUNTEER.ID.eq(id))

    fun getByLogin(login: String) = jooq.fetchOne(VOLUNTEER, VOLUNTEER.LOGIN.eq(login))

    fun all() = jooq.fetch(VOLUNTEER).into(VolunteerRecord::class.java)

    /**
     * @return registered [VolunteerRecord] on success, `null` on failure
     */
    fun register(login: String, passwordHash: String, canAccess: Boolean): VolunteerRecord? =
        jooq.selectFrom(
            DSL.finalTable(
                DSL.mergeInto(VOLUNTEER)
                    .using(jooq.selectOne())
                    .on(VOLUNTEER.LOGIN.eq(login))
                    .whenNotMatchedThenInsert()
                    .set(VOLUNTEER.LOGIN, login)
                    .set(VOLUNTEER.PASSWORD_HASH, passwordHash)
                    .set(VOLUNTEER.CAN_ACCESS, canAccess)
            )
        ).fetchOne()
}