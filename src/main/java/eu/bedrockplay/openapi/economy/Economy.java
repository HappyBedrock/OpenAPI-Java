package eu.bedrockplay.openapi.economy;

import cn.nukkit.Player;
import eu.bedrockplay.openapi.mysql.QueryQueue;
import eu.bedrockplay.openapi.mysql.query.AddCoinsQuery;
import eu.bedrockplay.openapi.mysql.query.FetchValueQuery;
import eu.bedrockplay.openapi.mysql.query.UpdateRowQuery;

import java.util.HashMap;
import java.util.function.Consumer;

public class Economy {

    public static void addCoins(Player player, int coins) {
        QueryQueue.submitQuery(new AddCoinsQuery(player.getName(), coins));
    }

    public static void removeCoins(Player player, int coins) {
        Economy.addCoins(player, -coins);
    }

    public static void setCoins(Player player, int coins) {
        QueryQueue.submitQuery(new UpdateRowQuery(new HashMap<String, Object>() {{ this.put("Coins", String.valueOf(coins)); }}, "Name", player.getName()));
    }

    public static void getCoins(Player player, Consumer<Integer> callback) {
        QueryQueue.submitQuery(new FetchValueQuery(player.getName(), "Coins"), (query -> callback.accept((Integer) ((FetchValueQuery)query).value)));
    }
}
