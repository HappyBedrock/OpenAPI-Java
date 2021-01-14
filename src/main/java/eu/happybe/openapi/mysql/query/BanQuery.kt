package eu.happybe.openapi.mysql.query

import eu.happybe.openapi.mysql.AsyncQuery
import java.sql.SQLException
import java.sql.Statement

class BanQuery(var player: String, var admin: String, var time: Int, var reason: String) : AsyncQuery() {
    @Throws(SQLException::class)
    override fun query(statement: Statement) {
        val result = statement.executeQuery("SELECT * FROM HB_Bans WHERE Name='" + player + "';")
        if (!result.next()) {
            statement.executeUpdate("INSERT INTO HB_Bans (Name, Admin, Time, Reason) VALUES ('" + player + "', '" + admin + "', '" + time + "', '" + reason + "');")
        } else {
            statement.executeUpdate("UPDATE HB_Bans SET Admin='" + admin + "', Time='" + time + "', Reason='" + reason + "' WHERE Name='" + player + "';")
        }
    }
}