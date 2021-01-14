package eu.happybe.openapi.mysql.query

import eu.happybe.openapi.mysql.AsyncQuery
import java.sql.SQLException
import java.sql.Statement
import java.util.*

class RemovePartyMemberQuery(var owner: String, var member: String) : AsyncQuery() {
    @Throws(SQLException::class)
    override fun query(statement: Statement) {
        val result = statement.executeQuery("SELECT * FROM HB_Parties WHERE Owner='" + owner + "';")
        if (!result.next()) {
            return
        }
        var members = result.getString("Members")
        var splitMembers = arrayOf<String?>()
        if (members != "") {
            splitMembers = members.split(",").toTypedArray()
        }
        val membersList: MutableList<String> = ArrayList(Arrays.asList(*splitMembers))
        membersList.remove(member)
        members = java.lang.String.join(",", membersList)
        statement.executeUpdate("UPDATE HB_Parties SET Members='" + members + "' WHERE Owner='" + owner + "';")
    }
}