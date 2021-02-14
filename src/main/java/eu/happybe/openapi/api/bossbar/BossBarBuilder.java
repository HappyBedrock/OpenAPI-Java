package eu.happybe.openapi.api.bossbar;

import io.gomint.entity.EntityPlayer;


public class BossBarBuilder {

    public static void sendBossBarText(EntityPlayer player, String text) {
        player.bossBar().title(text);
        player.bossBar().addPlayer(player);
    }

    public static void removeBossBar(EntityPlayer player) {
        player.bossBar().removePlayer(player);
    }
}
