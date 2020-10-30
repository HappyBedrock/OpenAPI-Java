package eu.happybe.openapi.mysql.query;

import eu.happybe.openapi.mysql.AsyncQuery;

import java.sql.SQLException;
import java.sql.Statement;

public class AddTokensQuery extends AsyncQuery {

    public String player;
    public int coins;

    public AddTokensQuery(String player, int coins) {
        this.player = player;
        this.coins = coins;
    }

    @Override
    public void query(Statement statement) throws SQLException {
        if(this.coins < 0) {
            statement.executeUpdate("UPDATE HB_Values SET Tokens=Tokens-" + Math.abs(this.coins) + " WHERE Name='"+this.player+"'");
            return;
        }
        statement.executeUpdate("UPDATE HB_Values SET Tokens=Tokens+" + coins + " WHERE Name='"+this.player+"';");
    }
}
