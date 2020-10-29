package eu.happybe.openapi.mysql.query;

import eu.happybe.openapi.mysql.AsyncQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LazyRegisterServerQuery extends AsyncQuery {

    public String serverName;
    public int serverPort;

    public LazyRegisterServerQuery(String serverName, int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;
    }

    @Override
    public void query(Statement statement) throws SQLException {
        ResultSet result = statement.executeQuery("SELECT * FROM HB_Servers WHERE ServerName='" + this.serverName + "';");
        if(!result.next()) {
            statement.executeUpdate("INSERT INTO HB_Servers (ServerName, ServerAlias, ServerPort, IsOnline) VALUES ('"+ this.serverName +"', '"+this.serverName+"', '"+this.serverPort+"', '1');");
        }

        statement.executeUpdate("UPDATE HB_Servers SET IsOnline='1',ServerPort='"+ this.serverPort + "' WHERE ServerName='" + this.serverName + "';");
    }
}
