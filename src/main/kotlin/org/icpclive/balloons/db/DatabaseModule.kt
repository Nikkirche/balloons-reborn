package org.icpclive.balloons.db

import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import java.sql.Connection

fun databaseModule(config: DatabaseConfig) =
    module {
        single { config.createConnection() }
        single { DSL.using(get<Connection>(), SQLDialect.H2) }
        singleOf(::BalloonRepository)
        singleOf(::VolunteerRepository)
    }
