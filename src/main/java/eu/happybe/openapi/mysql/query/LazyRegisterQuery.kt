package eu.happybe.openapi.mysql.query

import eu.happybe.openapi.mysql.AsyncQuery
import java.sql.SQLException
import java.sql.Statement
import java.util.concurrent.ConcurrentHashMap

class LazyRegisterQuery(var player: String) : AsyncQuery() {
    var row: MutableMap<String?, Any?> = ConcurrentHashMap()
    var parties: MutableMap<String?, Any?> = ConcurrentHashMap()
    @Throws(SQLException::class)
    override fun query(statement: Statement) {
        var result = statement.executeQuery("SELECT * FROM HB_Values WHERE Name='" + player + "';")
        if (!result.next()) {
            return
        }
        var columnCount = result.metaData.columnCount
        for (i in 1..columnCount) {
            val key = result.metaData.getColumnName(i)
            val value = result.getObject(i)
            if (key == null) {
                println("[OpenAPI/LazyReqisterQuery] Could not find column name at i=$i")
                continue
            }
            if (value == null) {
//                System.out.println("[OpenAPI/LazyRegisterQuery] Could not find object at i="+i+" and column="+key);
                continue
            }
            row[key] = value
        }
        result = statement.executeQuery("SELECT * FROM HB_Parties WHERE FIND_IN_SET('" + player + "', Members) or Owner='" + player + "';")
        if (!result.next()) {
            return
        }
        columnCount = result.metaData.columnCount
        for (i in 1..columnCount) {
            val key = result.metaData.getColumnName(i)
            val value = result.getObject(i)
            if (key == null) {
                println("[OpenAPI/LazyReqisterQuery] Could not find column name at i=$i")
                continue
            }
            parties[key] = value
        }
    }
}