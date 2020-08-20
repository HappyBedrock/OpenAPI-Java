package eu.bedrockplay.openapi.utils;

import cn.nukkit.Player;
import eu.bedrockplay.openapi.ranks.Rank;
import eu.bedrockplay.openapi.ranks.RankDatabase;

public class PlayerUtils {

    public static void updateNameTag(Player player) {
        PlayerUtils.updateNameTag(player, "§7");
    }

    public static void updateNameTag(Player player, String color) {
        Rank rank = RankDatabase.getPlayerRank(player);

        player.setNameTag(rank.getFormatForNameTag() + color + player.getName() + "\n§5" + DeviceData.getDeviceName(player));
    }
}
