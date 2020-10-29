package eu.happybe.openapi.mysql.query;

import eu.happybe.openapi.mysql.AsyncQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class FetchValueQuery extends AsyncQuery {

    public String player;

    public String key;
    public Object value;

    public String table;

    public FetchValueQuery(String player, String key) {
        this(player, key, "Values");
    }

    public FetchValueQuery(String player, String key, String table) {
        this.player = player;
        this.key = key;
        this.table = "HB_" + table;
    }

    @Override
    public void query(Statement statement) throws SQLException {
        ResultSet result = statement.executeQuery("SELECT " + this.key + " FROM " + this.table + " WHERE Name='" + this.player + "';");
        if(!result.next()) {
            return;
        }

        if(result.getMetaData().getColumnCount() > 0)
            this.value = result.getObject(1);
    }
}
