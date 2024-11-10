package org.icpclive.balloons.db

import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import java.nio.file.Path

fun databaseModule(location: Path) = module {
    single { H2Connection(location) }
    single { DSL.using(get<H2Connection>().connection, SQLDialect.H2) }
    singleOf(::BalloonRepository)
}