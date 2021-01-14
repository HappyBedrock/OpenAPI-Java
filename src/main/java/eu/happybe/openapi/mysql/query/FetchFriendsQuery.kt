package eu.happybe.openapi.mysql.query

import eu.happybe.openapi.mysql.AsyncQuery
import java.sql.SQLException
import java.sql.Statement
import java.util.*

class FetchFriendsQuery(var player: String) : AsyncQuery() {
    var friends: List<String?> = ArrayList()
    @Throws(SQLException::class)
    override fun query(statement: Statement) {
        val result = statement.executeQuery("SELECT * FROM HB_Friends WHERE Name='" + player + "';")
        val friends = result.getString("Friends")
        var splitFriends = arrayOf<String?>()
        if (friends != "") {
            splitFriends = friends.split(",").toTypedArray()
        }
        this.friends = Arrays.asList(*splitFriends)
    }
}