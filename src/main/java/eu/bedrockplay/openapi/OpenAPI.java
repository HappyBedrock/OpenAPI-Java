package eu.bedrockplay.openapi;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerLoginEvent;
import cn.nukkit.network.Network;
import cn.nukkit.plugin.PluginBase;
import eu.bedrockplay.openapi.mysql.DatabaseData;
import eu.bedrockplay.openapi.mysql.QueryQueue;
import eu.bedrockplay.openapi.mysql.query.LazyRegisterQuery;
import eu.bedrockplay.openapi.ranks.RankDatabase;
import eu.bedrockplay.openapi.scoreboard.packets.RemoveObjectivePacket;
import eu.bedrockplay.openapi.scoreboard.packets.SetDisplayObjectivePacket;
import eu.bedrockplay.openapi.scoreboard.packets.SetScorePacket;
import eu.bedrockplay.openapi.servers.ServerManager;
import lombok.Getter;

public class OpenAPI extends PluginBase implements Listener {

    @Getter
    private static OpenAPI instance;

    @Override
    public void onEnable() {
        OpenAPI.instance = this;
        this.saveResource("/config.yml");

        RankDatabase.init();
        ServerManager.init();

        this.getServer().getPluginManager().registerEvents(this, this);

        DatabaseData.update(
                (String) this.getConfig().get("mysql-host"),
                (String) this.getConfig().get("mysql-user"),
                (String) this.getConfig().get("mysql-password")
        );

        this.getServer().getNetwork().registerPacket((byte)0x6a, RemoveObjectivePacket.class);
        this.getServer().getNetwork().registerPacket((byte)0x6b, SetDisplayObjectivePacket.class);
        this.getServer().getNetwork().registerPacket((byte)0x6c, SetScorePacket.class);
    }

    @Override
    public void onDisable() {
        ServerManager.save();
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();

        QueryQueue.submitQuery(new LazyRegisterQuery(event.getPlayer().getName()), (query -> {
            RankDatabase.savePlayerRank(player, String.valueOf(((LazyRegisterQuery)query).row.get("Rank")));
        }));
    }
}
