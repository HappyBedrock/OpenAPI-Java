package eu.happybe.openapi.mysql.query;

import eu.happybe.openapi.mysql.AsyncQuery;

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
            String key = result.getMetaData().getColumnName(i);
            Object value = result.getObject(i);

            if(key == null) {
                System.out.println("[OpenAPI/LazyReqisterQuery] Could not find column name at i=" + i);
                continue;
            }
            if(value == null) {
//                System.out.println("[OpenAPI/LazyRegisterQuery] Could not find object at i="+i+" and column="+key);
                continue;
            }

            this.row.put(key, value);
        }
    }
}
