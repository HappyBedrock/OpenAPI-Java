package eu.happybe.openapi.mysql.query;

import eu.happybe.openapi.mysql.AsyncQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

public class AddFriendQuery extends AsyncQuery {

    public String player;
    public String newFriend;

    public boolean changed = false;

    public AddFriendQuery(String player, String newFriend) {
        this.player = player;
        this.newFriend = newFriend;
    }

    @Override
    public void query(Statement statement) throws SQLException {
        this.addFriend(statement, this.player, this.newFriend);
        this.addFriend(statement, this.newFriend, this.player);
    }

    private void addFriend(Statement statement, String player, String friend) throws SQLException {
        ResultSet result = statement.executeQuery("SELECT  * FROM HB_Friends WHERE Name='"+player+"';");
        if(!result.next()) {
            return;
        }

        String friendsList = result.getString("Friends");
        String[] friends = new String[]{};
        if(!friendsList.equals("")) {
            friends = friendsList.split(",");
        }

        List<String> list = Arrays.asList(friends);
        if(!list.contains(friend)) {
            list.add(friend);
            this.changed = true;
        }


        friendsList = String.join(",", list);
        statement.executeUpdate("UPDATE HB_Friends SET Friends='"+friendsList+"' WHERE Name='"+player+"';");
    }
}
