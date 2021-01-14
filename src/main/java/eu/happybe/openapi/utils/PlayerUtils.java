package eu.happybe.openapi.utils;

import cn.nukkit.Player;
import eu.happybe.openapi.ranks.Rank;
import eu.happybe.openapi.ranks.RankDatabase;

public class PlayerUtils {

    public static void updateNameTag(Player player) {
        PlayerUtils.updateNameTag(player, player.namedTag.exist("NameTagColor") ? player.namedTag.getString("NameTagColor") : "§e");
    }

    public static void updateNameTag(Player player, String color) {
        Rank rank = RankDatabase.getPlayerRank(player);
        if(rank == null) {
            rank = RankDatabase.getRankByName("Guest");
        }

        player.namedTag.putString("NameTagColor", color);
        player.setNameTag(rank.getFormatForNameTag() + color + player.getName() + "\n§b" + DeviceData.getDeviceName(player));
    }
}
