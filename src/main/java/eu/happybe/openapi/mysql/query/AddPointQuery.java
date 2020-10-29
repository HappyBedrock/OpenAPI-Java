package eu.happybe.openapi.mysql.query;

import eu.happybe.openapi.mysql.AsyncQuery;

import java.sql.SQLException;
import java.sql.Statement;

public class AddPointQuery extends AsyncQuery {

    public String player;
    public String key;
    public String table;

    public AddPointQuery(String player, String key) {
        this(player, key, "Values");
    }

    public AddPointQuery(String player, String key, String table) {
        this.player = player;
        this.key = key;
        this.table = "HB_" + table;
    }

    @Override
    public void query(Statement statement) throws SQLException {
        statement.executeUpdate("UPDATE " + table + " SET " + key + "=" + key + "+1 WHERE Name='" + this.player + "';");
    }
}
