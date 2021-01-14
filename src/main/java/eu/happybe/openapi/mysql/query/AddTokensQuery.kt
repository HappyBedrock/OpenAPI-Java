package eu.happybe.openapi.mysql.query

import eu.happybe.openapi.mysql.AsyncQuery
import java.sql.SQLException
import java.sql.Statement

class AddTokensQuery(var player: String, var coins: Int) : AsyncQuery() {
    @Throws(SQLException::class)
    override fun query(statement: Statement) {
        if (coins < 0) {
            statement.executeUpdate("UPDATE HB_Values SET Tokens=Tokens-" + Math.abs(coins) + " WHERE Name='" + player + "'")
            return
        }
        statement.executeUpdate("UPDATE HB_Values SET Tokens=Tokens+" + coins + " WHERE Name='" + player + "';")
    }
}