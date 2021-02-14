package eu.happybe.openapi.mysql.query;

import eu.happybe.openapi.mysql.AsyncQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RemovePartyMemberQuery extends AsyncQuery {

    public String owner;
    public String member;

    public RemovePartyMemberQuery(String owner, String member) {
        this.owner = owner;
        this.member = member;
    }

    @Override
    public void query(Statement statement) throws SQLException {
        ResultSet result = statement.executeQuery("SELECT * FROM HB_Parties WHERE Owner='"+this.owner+"';");
        if(!result.next()) {
            return;
        }

        String members = result.getString("Members");
        String[] splitMembers = new String[] {};
        if(!members.equals("")) {
            splitMembers = members.split(",");
        }

        List<String> membersList = new ArrayList<>(Arrays.asList(splitMembers));
        membersList.remove(this.member);

        members = String.join(",", membersList);

        statement.executeUpdate("UPDATE HB_Parties SET Members='"+members+"' WHERE Owner='"+this.owner+"';");
    }
}
