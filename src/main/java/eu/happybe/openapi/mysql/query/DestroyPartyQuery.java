package eu.happybe.openapi.mysql.query;

import eu.happybe.openapi.mysql.AsyncQuery;

import java.sql.SQLException;
import java.sql.Statement;

public class DestroyPartyQuery extends AsyncQuery {

    public String owner;

    public DestroyPartyQuery(String owner) {
        this.owner = owner;
    }

    @Override
    public void query(Statement statement) throws SQLException {
        statement.executeUpdate("DELETE FROM HB_Parties WHERE Owner='"+this.owner+"';");
    }
}
