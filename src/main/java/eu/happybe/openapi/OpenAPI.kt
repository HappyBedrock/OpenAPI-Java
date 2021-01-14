package eu.happybe.openapi

import cn.nukkit.event.EventHandler
import cn.nukkit.event.Listener
import cn.nukkit.event.player.PlayerLoginEvent
import cn.nukkit.event.player.PlayerQuitEvent
import cn.nukkit.event.server.DataPacketReceiveEvent
import cn.nukkit.network.protocol.ModalFormResponsePacket
import cn.nukkit.plugin.PluginBase
import eu.happybe.openapi.bossbar.BossBarBuilder
import eu.happybe.openapi.event.LoginQueryReceiveEvent
import eu.happybe.openapi.form.FormQueue
import eu.happybe.openapi.mysql.AsyncQuery
import eu.happybe.openapi.mysql.DatabaseData
import eu.happybe.openapi.mysql.QueryQueue
import eu.happybe.openapi.mysql.query.LazyRegisterQuery
import eu.happybe.openapi.party.PartyManager
import eu.happybe.openapi.ranks.RankDatabase
import eu.happybe.openapi.scoreboard.packets.RemoveObjectivePacket
import eu.happybe.openapi.scoreboard.packets.SetDisplayObjectivePacket
import eu.happybe.openapi.scoreboard.packets.SetScorePacket
import eu.happybe.openapi.servers.ServerManager
import eu.happybe.openapi.utils.PlayerUtils
import lombok.Getter

class OpenAPI : PluginBase(), Listener {
    override fun onEnable() {
        instance = this
        this.saveResource("/config.yml")
        server.pluginManager.registerEvents(this, this)
        DatabaseData.update(
                config.getString("mysql-host"),
                config.getString("mysql-user"),
                config.getString("mysql-password")
        )
        RankDatabase.init()
        ServerManager.init()
        server.network.registerPacket(0x6a.toByte(), RemoveObjectivePacket::class.java)
        server.network.registerPacket(0x6b.toByte(), SetDisplayObjectivePacket::class.java)
        server.network.registerPacket(0x6c.toByte(), SetScorePacket::class.java)
    }

    override fun onDisable() {
        ServerManager.save()
    }

    @EventHandler
    fun onLogin(event: PlayerLoginEvent) {
        val player = event.player
        QueryQueue.submitQuery(LazyRegisterQuery(event.player.name)) { query: AsyncQuery ->
            RankDatabase.savePlayerRank(player, (query as LazyRegisterQuery).row["Rank"].toString())
            PartyManager.handleLoginQuery(player, query)
            PlayerUtils.updateNameTag(player)
            server.pluginManager.callEvent(LoginQueryReceiveEvent(player, query))
        }
    }

    @EventHandler
    fun onFormHandle(event: DataPacketReceiveEvent) {
        if (event.packet is ModalFormResponsePacket) {
            if (FormQueue.handleFormDataPacket(event.player, event.packet as ModalFormResponsePacket)) {
                event.setCancelled()
            }
        }
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        BossBarBuilder.removeBossBar(event.player)
        FormQueue.handleQuit(event.player)
        PartyManager.handleQuit(event.player)
    }

    companion object {
        @Getter
        private var instance: OpenAPI? = null
    }
}