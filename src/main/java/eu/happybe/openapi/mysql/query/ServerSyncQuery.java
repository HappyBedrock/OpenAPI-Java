package eu.happybe.openapi.mysql.query;

import eu.happybe.openapi.mysql.AsyncQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerSyncQuery extends AsyncQuery {

    public String currentServer;
    public int onlinePlayers;

    public List<Map<String, Object>> table = new ArrayList<>();

    public ServerSyncQuery(String currentServer, int onlinePlayers) {
        this.currentServer = currentServer;
        this.onlinePlayers = onlinePlayers;
    }

    @Override
    public void query(Statement statement) throws SQLException {
        statement.executeUpdate("UPDATE HB_Servers SET OnlinePlayers='" + this.onlinePlayers + "' WHERE ServerName='" + this.currentServer + "';");

        ResultSet result = statement.executeQuery("SELECT * FROM HB_Servers;");
        while (result.next()) {
            Map<String, Object> row = new ConcurrentHashMap<>();
            for (int i = 1; i <= result.getMetaData().getColumnCount(); i++) {
                row.put(result.getMetaData().getColumnName(i), result.getObject(i));
            }

            this.table.add(row);
        }
    }
}
