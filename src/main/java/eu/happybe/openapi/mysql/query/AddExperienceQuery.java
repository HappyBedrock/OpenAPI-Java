package eu.happybe.openapi.mysql.query;

import eu.happybe.openapi.mysql.AsyncQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AddExperienceQuery extends AsyncQuery {

    public String player;
    public int experience;

    public boolean levelUp = false;
    public int newLevel = 0;

    public AddExperienceQuery(String player, int experience) {
        this.player = player;
        this.experience = experience;
    }

    @Override
    public void query(Statement statement) throws SQLException {
        ResultSet result = statement.executeQuery("SELECT * FROM HB_Values WHERE Name='"+this.player+"';");
        if(!result.next()) {
            return;
        }

        int currentExp = result.getInt("Experience");
        int currentLevel = result.getInt("Level");

        int requiredExp = 100 + (currentLevel * 50);
        if(currentExp + this.experience >= requiredExp) {
            this.levelUp = true;
            this.newLevel = currentLevel + 1;

            currentExp += this.experience - requiredExp;

            statement.executeUpdate("UPDATE HB_Values SET Experience='" + currentExp + "',Level='" + this.newLevel + "' WHERE Name='" + this.player +"';");
            return;
        }

        statement.executeUpdate("UPDATE HB_Values SET Experience=Experience+" + this.experience + " WHERE Name='" + this.player + "';");
    }
}
