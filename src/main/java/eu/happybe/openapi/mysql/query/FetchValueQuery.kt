package eu.happybe.openapi.mysql.query

import eu.happybe.openapi.mysql.AsyncQuery
import java.sql.SQLException
import java.sql.Statement

class FetchValueQuery @JvmOverloads constructor(var player: String, var key: String, table: String = "Values") : AsyncQuery() {
    var value: Any? = null
    var table: String
    @Throws(SQLException::class)
    override fun query(statement: Statement) {
        val result = statement.executeQuery("SELECT " + key + " FROM " + table + " WHERE Name='" + player + "';")
        if (!result.next()) {
            return
        }
        if (result.metaData.columnCount > 0) value = result.getObject(1)
    }

    init {
        this.table = "HB_$table"
    }
}