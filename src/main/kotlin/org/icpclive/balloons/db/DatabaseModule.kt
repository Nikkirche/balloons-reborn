package org.icpclive.balloons.db

import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import java.nio.file.Path
import java.sql.Connection
import java.sql.DriverManager
import kotlin.io.path.absolutePathString

fun databaseModule(location: Path) =
    module {
        single {
            DriverManager.getConnection(
                "jdbc:h2:${location.absolutePathString()};INIT=RUNSCRIPT FROM 'classpath:schema.sql';AUTO_SERVER=TRUE",
            )
        }
        single { DSL.using(get<Connection>(), SQLDialect.H2) }
        singleOf(::BalloonRepository)
        singleOf(::VolunteerRepository)
    }
