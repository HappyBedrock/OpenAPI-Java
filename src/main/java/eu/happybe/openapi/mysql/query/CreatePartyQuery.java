package eu.happybe.openapi.mysql.query;

import eu.happybe.openapi.mysql.AsyncQuery;

import java.sql.SQLException;
import java.sql.Statement;

public class CreatePartyQuery extends AsyncQuery {

    public String owner;
    public String server;

    public CreatePartyQuery(String owner, String server) {
        this.owner = owner;
        this.server = server;
    }

    @Override
    public void query(Statement statement) throws SQLException {

    }
}
