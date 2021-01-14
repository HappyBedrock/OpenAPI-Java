package eu.happybe.openapi.mysql.query

import eu.happybe.openapi.mysql.AsyncQuery
import java.sql.SQLException
import java.sql.Statement

class LazyRegisterServerQuery(var serverName: String, var serverPort: Int) : AsyncQuery() {
    @Throws(SQLException::class)
    override fun query(statement: Statement) {
        val result = statement.executeQuery("SELECT * FROM HB_Servers WHERE ServerName='" + serverName + "';")
        if (!result.next()) {
            statement.executeUpdate("INSERT INTO HB_Servers (ServerName, ServerAlias, ServerPort, IsOnline) VALUES ('" + serverName + "', '" + serverName + "', '" + serverPort + "', '1');")
        }
        statement.executeUpdate("UPDATE HB_Servers SET IsOnline='1',ServerPort='" + serverPort + "' WHERE ServerName='" + serverName + "';")
    }
}