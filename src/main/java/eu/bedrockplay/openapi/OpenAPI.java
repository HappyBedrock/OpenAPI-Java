package eu.bedrockplay.openapi;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerLoginEvent;
import cn.nukkit.plugin.PluginBase;
import eu.bedrockplay.openapi.mysql.DatabaseData;
import eu.bedrockplay.openapi.mysql.QueryQueue;
import eu.bedrockplay.openapi.mysql.query.LazyRegisterQuery;
import eu.bedrockplay.openapi.ranks.RankDatabase;
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
                (String) this.getConfig().get("mysql-host"),
                (String) this.getConfig().get("mysql-user"),
                (String) this.getConfig().get("mysql-password")
        );
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();

        QueryQueue.submitQuery(new LazyRegisterQuery(event.getPlayer().getName()), (query -> {
            RankDatabase.savePlayerRank(player, String.valueOf(((LazyRegisterQuery)query).row.get("Rank")));
        }));
    }
}
