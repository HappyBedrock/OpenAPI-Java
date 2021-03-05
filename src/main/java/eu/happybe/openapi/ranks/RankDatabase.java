package eu.happybe.openapi.ranks;

import cn.nukkit.Player;
import eu.happybe.openapi.OpenAPI;
import eu.happybe.openapi.mysql.QueryQueue;
import eu.happybe.openapi.mysql.query.UpdateRowQuery;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RankDatabase {

    private static final Map<String, Rank> ranks = new HashMap<>();

    public static void init() {
        List<Rank> ranks = Arrays.asList(
                // Staff
                new Rank("Owner", "§6§lOWNER", new String[]{"happybe.operator", "nukkit.command.gamemode", "nukkit.command.teleport", "nukkit.command.kick"}),
                new Rank("Developer", "§6§lDEVELOPER", new String[] {"happybe.operator"}),
                new Rank("Admin", "§6§lADMIN", new String[] {"happybe.operator", "nukkit.command.teleport", "nukkit.command.kick"}),
                new Rank("Mod", "§e§lMOD", new String[] {"happybe.moderator", "nukkit.command.teleport", "nukkit.command.kick"}),
                new Rank("Helper", "§e§lHELPER", new String[] {"happybe.helper", "nukkit.command.kick"}),
                new Rank("Builder", "§e§lBUILDER", new String[] {"happybe.builder"}),
                // Buyable ranks
                new Rank("Bedrock", "§9§lBEDROCK", new String[] {"happybe.bedrock"}),
                new Rank("MVP", "§3§lMVP§B+", new String[] {"happybe.mvp"}),
                new Rank("VIP", "§b§lVIP", new String[] {"happybe.vip"}),
                // Gettable ranks
                new Rank("YouTube", "§c§lYOUTUBE", new String[] {"happybe.bedrock"}),
                new Rank("Voter", "§b§lVOTER", new String[] {"happybe.voter"}),
                // Guest
                new Rank("Guest", "", new String[] {}, false)
        );

        for(Rank rank : ranks) {
            RankDatabase.ranks.put(rank.getRankName().toLowerCase(), rank);
        }
    }

    public static void savePlayerRank(Player player, String rank) {
        RankDatabase.savePlayerRank(player, rank, false);
    }

    public static void savePlayerRank(Player player, String rank, boolean saveToDatabase) {
        Rank targetRank = RankDatabase.getRankByName(rank);
        if(targetRank == null) {
            player.kick("Invalid rank (" + rank + ")");
            OpenAPI.getInstance().getLogger().error("Invalid rank received from database (" + rank + ") for player " + player.getName() + "!");
            return;
        }

        player.namedTag.putString("Rank", targetRank.getRankName());

        player.recalculatePermissions();
        for(String permission : targetRank.getPermissions()) {
            player.addAttachment(OpenAPI.getInstance(), permission, true);
        }

        if(saveToDatabase) {
            QueryQueue.submitQuery(new UpdateRowQuery(new HashMap<String, Object>() {{ this.put("Rank", targetRank.getRankName()); }}, "Name", player.getName()));
        }
    }

    public static Rank getPlayerRank(Player player) {
        return RankDatabase.getRankByName(player.namedTag.getString("Rank"));
    }

    public static Rank getRankByName(String name) {
        return ranks.get(name.toLowerCase());
    }
}
