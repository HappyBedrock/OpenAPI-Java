package eu.happybe.openapi.economy;

import cn.nukkit.Player;
import eu.happybe.openapi.mysql.QueryQueue;
import eu.happybe.openapi.mysql.query.AddTokensQuery;
import eu.happybe.openapi.mysql.query.FetchValueQuery;
import eu.happybe.openapi.mysql.query.UpdateRowQuery;

import java.util.HashMap;
import java.util.function.Consumer;

public class Economy {

    public static void addTokens(Player player, int coins) {
        QueryQueue.submitQuery(new AddTokensQuery(player.getName(), coins));
    }

    public static void removeTokens(Player player, int coins) {
        Economy.addTokens(player, -coins);
    }

    public static void setTokens(Player player, int coins) {
        QueryQueue.submitQuery(new UpdateRowQuery(new HashMap<String, Object>() {{ this.put("Tokens", String.valueOf(coins)); }}, "Name", player.getName()));
    }

    public static void getTokens(Player player, Consumer<Integer> callback) {
        QueryQueue.submitQuery(new FetchValueQuery(player.getName(), "Tokens"), (query -> callback.accept((Integer) ((FetchValueQuery)query).value)));
    }
}
