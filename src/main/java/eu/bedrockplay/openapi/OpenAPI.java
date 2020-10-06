package eu.bedrockplay.openapi;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerLoginEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.network.protocol.ModalFormResponsePacket;
import cn.nukkit.plugin.PluginBase;
import eu.bedrockplay.openapi.bossbar.BossBarBuilder;
import eu.bedrockplay.openapi.form.FormQueue;
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

        this.getServer().getPluginManager().registerEvents(this, this);

        DatabaseData.update(
            this.getConfig().getString("mysql-host"),
            this.getConfig().getString("mysql-user"),
            this.getConfig().getString("mysql-password")
        );

        RankDatabase.init();
        ServerManager.init();

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

    @EventHandler
    public void onFormHandle(DataPacketReceiveEvent event) {
        if(event.getPacket() instanceof ModalFormResponsePacket) {
            if(FormQueue.handleFormDataPacket(event.getPlayer(), (ModalFormResponsePacket) event.getPacket())) {
                event.setCancelled();
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        BossBarBuilder.removeBossBar(event.getPlayer());
        FormQueue.handleQuit(event.getPlayer());
    }
}
