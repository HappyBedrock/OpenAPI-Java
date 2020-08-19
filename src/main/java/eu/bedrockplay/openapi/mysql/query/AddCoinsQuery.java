package eu.bedrockplay.openapi.mysql.query;

import eu.bedrockplay.openapi.mysql.AsyncQuery;

import java.sql.SQLException;
import java.sql.Statement;

public class AddCoinsQuery extends AsyncQuery {

    public String player;
    public int coins;

    public AddCoinsQuery(String player, int coins) {
        this.player = player;
        this.coins = coins;
    }

    @Override
    public void query(Statement statement) throws SQLException {
        if(this.coins < 0) {
            statement.executeUpdate("UPDATE BP_Values SET Coins=Coins-" + Math.abs(this.coins) + " WHERE Name='"+this.player+"'");
            return;
        }
        statement.executeUpdate("UPDATE BP_Values SET Coins=Coins+" + coins + " WHERE Name='"+this.player+"';");
    }
}
