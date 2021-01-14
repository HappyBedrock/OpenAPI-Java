package eu.happybe.openapi.mysql.query

import eu.happybe.openapi.mysql.AsyncQuery
import java.sql.SQLException
import java.sql.Statement
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class ServerSyncQuery(var currentServer: String, var onlinePlayers: Int) : AsyncQuery() {
    var table: MutableList<Map<String, Any>> = ArrayList()
    @Throws(SQLException::class)
    override fun query(statement: Statement) {
        statement.executeUpdate("UPDATE HB_Servers SET OnlinePlayers='" + onlinePlayers + "' WHERE ServerName='" + currentServer + "';")
        val result = statement.executeQuery("SELECT * FROM HB_Servers;")
        while (result.next()) {
            val row: MutableMap<String, Any> = ConcurrentHashMap()
            for (i in 1..result.metaData.columnCount) {
                row[result.metaData.getColumnName(i)] = result.getObject(i)
            }
            table.add(row)
        }
    }
}