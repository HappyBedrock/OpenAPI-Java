package eu.happybe.openapi;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerLoginEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.network.protocol.ModalFormResponsePacket;
import cn.nukkit.plugin.PluginBase;
import eu.happybe.openapi.bossbar.BossBarBuilder;
import eu.happybe.openapi.event.LoginQueryReceiveEvent;
import eu.happybe.openapi.form.FormQueue;
import eu.happybe.openapi.mysql.DatabaseData;
import eu.happybe.openapi.mysql.QueryQueue;
import eu.happybe.openapi.mysql.query.LazyRegisterQuery;
import eu.happybe.openapi.party.PartyManager;
import eu.happybe.openapi.ranks.RankDatabase;
import eu.happybe.openapi.scoreboard.packets.RemoveObjectivePacket;
import eu.happybe.openapi.scoreboard.packets.SetDisplayObjectivePacket;
import eu.happybe.openapi.scoreboard.packets.SetScorePacket;
import eu.happybe.openapi.servers.ServerManager;
import eu.happybe.openapi.utils.PlayerUtils;
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
            PartyManager.handleLoginQuery(player, (LazyRegisterQuery) query);
            PlayerUtils.updateNameTag(player);
            this.getServer().getPluginManager().callEvent(new LoginQueryReceiveEvent(player, (LazyRegisterQuery) query));
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
        PartyManager.handleQuit(event.getPlayer());
    }
}
