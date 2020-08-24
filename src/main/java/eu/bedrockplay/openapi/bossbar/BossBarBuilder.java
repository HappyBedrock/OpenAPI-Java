package eu.bedrockplay.openapi.bossbar;

import cn.nukkit.Player;
import cn.nukkit.utils.DummyBossBar;

import java.util.HashMap;
import java.util.Map;

public class BossBarBuilder {

    private static final Map<String, DummyBossBar> bossBars = new HashMap<>();

    public static void sendBossBarText(Player player, String text) {
        if(!BossBarBuilder.bossBars.containsKey(player.getName())) {
            DummyBossBar bossBar = new DummyBossBar.Builder(player).text(text).length(100F).build();
            bossBar.create();
            bossBar.reshow();

            BossBarBuilder.bossBars.put(player.getName(), bossBar);
            return;
        }

        BossBarBuilder.bossBars.get(player.getName()).setText(text);
    }

    public static void removeBossBar(Player player) {
        BossBarBuilder.bossBars.get(player.getName()).destroy();
    }
}
