package eu.happybe.openapi.api.economy;

import eu.happybe.openapi.mysql.QueryQueue;
import eu.happybe.openapi.mysql.query.AddTokensQuery;
import eu.happybe.openapi.mysql.query.FetchValueQuery;
import eu.happybe.openapi.mysql.query.UpdateRowQuery;
import io.gomint.entity.EntityPlayer;

import java.util.HashMap;
import java.util.function.Consumer;

public class Economy {

    public static void addTokens(EntityPlayer player, int coins) {
        QueryQueue.submitQuery(new AddTokensQuery(player.name(), coins));
    }

    public static void removeTokens(EntityPlayer player, int coins) {
        Economy.addTokens(player, -coins);
    }

    public static void setTokens(EntityPlayer player, int coins) {
        QueryQueue.submitQuery(new UpdateRowQuery(new HashMap<>() {{
            this.put("Tokens", String.valueOf(coins));
        }}, "Name", player.name()));
    }

    public static void getTokens(EntityPlayer player, Consumer<Integer> callback) {
        QueryQueue.submitQuery(new FetchValueQuery(player.name(), "Tokens"), (query -> callback.accept((Integer) ((FetchValueQuery)query).value)));
    }
}
