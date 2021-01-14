package eu.happybe.openapi.mysql.query

import eu.happybe.openapi.mysql.AsyncQuery
import java.sql.SQLException
import java.sql.Statement

class AddPointQuery @JvmOverloads constructor(var player: String, var key: String, table: String = "Values") : AsyncQuery() {
    var table: String
    @Throws(SQLException::class)
    override fun query(statement: Statement) {
        statement.executeUpdate("UPDATE " + table + " SET " + key + "=" + key + "+1 WHERE Name='" + player + "';")
    }

    init {
        this.table = "HB_$table"
    }
}