package eu.happybe.openapi.mysql.query

import eu.happybe.openapi.mysql.AsyncQuery
import java.sql.SQLException
import java.sql.Statement

class CreatePartyQuery(var owner: String, var server: String) : AsyncQuery() {
    @Throws(SQLException::class)
    override fun query(statement: Statement) {
    }
}