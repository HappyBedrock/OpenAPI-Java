package eu.happybe.openapi.mysql.query

import eu.happybe.openapi.mysql.AsyncQuery
import java.sql.SQLException
import java.sql.Statement
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class UpdateRowQuery @JvmOverloads constructor(updates: Map<String, Any>?, conditionKey: String, conditionValue: String, table: String = "Values") : AsyncQuery() {
    var updates: MutableMap<String, Any> = ConcurrentHashMap()
    var conditionKey: String
    var conditionValue: String
    var table: String
    @Throws(SQLException::class)
    override fun query(statement: Statement) {
        val updates: MutableList<String> = ArrayList()
        for (key in this.updates.keys) {
            updates.add(key + "='" + this.updates[key] + "'")
        }
        statement.executeUpdate("UPDATE " + table + " SET " + java.lang.String.join(",", updates) + " WHERE " + conditionKey + "='" + conditionValue + "';")
    }

    init {
        this.updates.putAll(updates!!)
        this.conditionKey = conditionKey
        this.conditionValue = conditionValue
        this.table = "HB_$table"
    }
}