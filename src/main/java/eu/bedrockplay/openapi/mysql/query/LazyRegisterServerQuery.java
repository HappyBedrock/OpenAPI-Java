package eu.bedrockplay.openapi.mysql.query;

import eu.bedrockplay.openapi.mysql.AsyncQuery;

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

    }
}
