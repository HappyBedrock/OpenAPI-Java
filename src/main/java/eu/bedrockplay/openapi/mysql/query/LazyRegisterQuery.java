package eu.bedrockplay.openapi.mysql.query;

import eu.bedrockplay.openapi.mysql.AsyncQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LazyRegisterQuery extends AsyncQuery {

    public String player;

    public Map<String, Object> row = new ConcurrentHashMap<>();

    public LazyRegisterQuery(String player) {
        this.player = player;
    }

    @Override
    public void query(Statement statement) throws SQLException {
        ResultSet result = statement.executeQuery("SELECT * FROM BP_Values WHERE Name='" + this.player + "';");
        if(!result.next()) {
            return;
        }

        int columnCount = result.getMetaData().getColumnCount();
        for(int i = 1; i <= columnCount; i++) {
            this.row.put(result.getMetaData().getColumnName(i), result.getObject(i));
        }
    }
}
