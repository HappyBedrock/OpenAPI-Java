package eu.happybe.openapi.mysql.query

import eu.happybe.openapi.mysql.AsyncQuery
import java.sql.SQLException
import java.sql.Statement
import java.util.*

class AddFriendQuery(var player: String, var newFriend: String) : AsyncQuery() {
    var changed = false
    @Throws(SQLException::class)
    override fun query(statement: Statement) {
        addFriend(statement, player, newFriend)
        addFriend(statement, newFriend, player)
    }

    @Throws(SQLException::class)
    private fun addFriend(statement: Statement, player: String, friend: String) {
        val result = statement.executeQuery("SELECT  * FROM HB_Friends WHERE Name='$player';")
        if (!result.next()) {
            return
        }
        var friendsList = result.getString("Friends")
        var friends = arrayOf<String?>()
        if (friendsList != "") {
            friends = friendsList.split(",").toTypedArray()
        }
        val list = Arrays.asList(*friends)
        if (!list.contains(friend)) {
            list.add(friend)
            changed = true
        }
        friendsList = java.lang.String.join(",", list)
        statement.executeUpdate("UPDATE HB_Friends SET Friends='$friendsList' WHERE Name='$player';")
    }
}