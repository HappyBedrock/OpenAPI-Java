package eu.happybe.openapi.mysql.query;

import eu.happybe.openapi.mysql.AsyncQuery;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UpdateRowQuery extends AsyncQuery {

    public Map<String, Object> updates = new ConcurrentHashMap<>();

    public String conditionKey;
    public String conditionValue;

    public String table;

    public UpdateRowQuery(Map<String, Object> updates, String conditionKey, String conditionValue) {
        this(updates, conditionKey, conditionValue, "Values");
    }

    public UpdateRowQuery(Map<String, Object> updates, String conditionKey, String conditionValue, String table) {
        this.updates.putAll(updates);
        this.conditionKey = conditionKey;
        this.conditionValue = conditionValue;
        this.table = "HB_" + table;
    }

    @Override
    public void query(Statement statement) throws SQLException {
        List<String> updates = new ArrayList<>();
        for(String key : this.updates.keySet()) {
            updates.add(key + "='" + this.updates.get(key) + "'");
        }

        statement.executeUpdate("UPDATE " + this.table + " SET " + String.join(",", updates) + " WHERE " + this.conditionKey + "='" + this.conditionValue + "';");
    }
}
