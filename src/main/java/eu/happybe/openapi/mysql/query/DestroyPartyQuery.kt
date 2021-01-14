package eu.happybe.openapi.mysql.query

import eu.happybe.openapi.mysql.AsyncQuery
import java.sql.SQLException
import java.sql.Statement

class DestroyPartyQuery(var owner: String) : AsyncQuery() {
    @Throws(SQLException::class)
    override fun query(statement: Statement) {
        statement.executeUpdate("DELETE FROM HB_Parties WHERE Owner='" + owner + "';")
    }
}