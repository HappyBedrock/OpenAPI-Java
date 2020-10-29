package eu.happybe.openapi.mysql.query;

import eu.happybe.openapi.mysql.AsyncQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class BanQuery extends AsyncQuery {

    public String player;
    public String admin;
    public int time;
    public String reason;

    public BanQuery(String player, String admin, int time, String reason) {
        this.player = player;
        this.admin = admin;
        this.time = time;
        this.reason = reason;
    }

    @Override
    public void query(Statement statement) throws SQLException {
        ResultSet result = statement.executeQuery("SELECT * FROM HB_Bans WHERE Name='"+this.player+"';");
        if(!result.next()) {
            statement.executeUpdate("INSERT INTO HB_Bans (Name, Admin, Time, Reason) VALUES ('"+this.player+"', '"+ this.admin + "', '" + this.time + "', '" + this.reason + "');");
        }
        else {
            statement.executeUpdate("UPDATE HB_Bans SET Admin='"+this.admin+"', Time='"+this.time+"', Reason='"+this.reason+"' WHERE Name='"+this.player+"';");
        }
    }
}
