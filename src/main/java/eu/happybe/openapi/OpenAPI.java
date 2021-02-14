package eu.happybe.openapi;

import eu.happybe.openapi.api.bossbar.BossBarBuilder;
import eu.happybe.openapi.api.form.FormQueue;
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
import io.gomint.config.YamlConfig;
import io.gomint.event.EventListener;
import io.gomint.plugin.Plugin;
import lombok.Getter;
import lombok.SneakyThrows;

public class OpenAPI extends Plugin implements EventListener {

    @Getter
    private static OpenAPI instance;

    @SneakyThrows
    @Override
    public void onStartup() {
        OpenAPI.instance = this;
        // TODO - Save config

        this.registerListener(this);

        YamlConfig config = new YamlConfig(this.dataFolder() + "/config.yml").load().saveToMap();
        DatabaseData.update();
    }

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
