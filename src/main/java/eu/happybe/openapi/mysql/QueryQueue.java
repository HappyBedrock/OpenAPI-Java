package eu.happybe.openapi.mysql;

import eu.happybe.openapi.OpenAPI;
import io.gomint.scheduler.Task;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class QueryQueue {

    private static final Map<AsyncQuery, Consumer<AsyncQuery>> callbacks = new HashMap<>();

    public static void submitQuery(AsyncQuery query) {
        QueryQueue.submitQuery(query, null);
    }

    public static void submitQuery(AsyncQuery query, Consumer<AsyncQuery> callback) {
        query.host = DatabaseData.getHost();
        query.user = DatabaseData.getUser();
        query.password = DatabaseData.getPassword();

        Task task = OpenAPI.getInstance().scheduler().executeAsync(query);
        task.onComplete(query::onCompletion);

        if(callback != null) {
            QueryQueue.callbacks.put(query, callback);
        }
    }

    public static void activateCallback(AsyncQuery query) {
        if(!callbacks.containsKey(query)) {
            return;
        }

        Consumer<AsyncQuery> callback = QueryQueue.callbacks.get(query);
        callback.accept(query);
    }
}
