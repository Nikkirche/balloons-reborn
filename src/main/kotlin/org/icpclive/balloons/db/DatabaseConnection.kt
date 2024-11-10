package org.icpclive.balloons.db

import java.sql.DriverManager

class DatabaseConnection {
    // TODO set dynamic URL
    private val database = DriverManager.getConnection("jdbc:h2:./db.h2")

    // TODO launch server
}
