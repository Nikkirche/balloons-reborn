package org.icpclive.balloons.db

import java.io.Closeable
import java.nio.file.Path
import java.sql.Connection
import java.sql.DriverManager
import kotlin.io.path.absolutePathString

class H2Connection(private val location: Path) : Closeable {
    val connection: Connection =
        DriverManager.getConnection("jdbc:h2:${location.absolutePathString()};INIT=RUNSCRIPT FROM 'classpath:schema.sql';AUTO_SERVER=TRUE")

    override fun close() {
        connection.close()
    }
}
