package eu.happybe.openapi.mysql.query;

import eu.happybe.openapi.mysql.AsyncQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FetchFriendsQuery extends AsyncQuery {

    public String player;
    public List<String> friends = new ArrayList<>();

    public FetchFriendsQuery(String player) {
        this.player = player;
    }

    @Override
    public void query(Statement statement) throws SQLException {
        ResultSet result = statement.executeQuery("SELECT * FROM HB_Friends WHERE Name='"+this.player+"';");

        String friends = result.getString("Friends");
        String[] splitFriends = new String[] {};
        if(!friends.equals("")) {
            splitFriends = friends.split(",");
        }

        this.friends = Arrays.asList(splitFriends);
    }
}
