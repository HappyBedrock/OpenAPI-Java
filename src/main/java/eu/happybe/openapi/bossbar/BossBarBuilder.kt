package eu.happybe.openapi.bossbar

import cn.nukkit.Player
import cn.nukkit.entity.Entity
import cn.nukkit.entity.data.EntityMetadata
import cn.nukkit.network.protocol.AddEntityPacket
import cn.nukkit.network.protocol.BossEventPacket
import cn.nukkit.network.protocol.RemoveEntityPacket
import cn.nukkit.network.protocol.SetEntityDataPacket
import java.util.*

object BossBarBuilder {
    private var bossBarEid: Long = -1
        private get() {
            if (field == -1L) {
                field = Entity.entityCount++
            }
            return field
        }
    private val bossBars: MutableMap<String, String> = HashMap()
    fun sendBossBarText(player: Player, text: String) {
        if (!bossBars.containsKey(player.name)) {
            bossBars[player.name] = text
            createBossEntity(player, text)
            showBossBar(player, text)
            return
        }
        if (bossBars[player.name] == text) {
            return
        }
        updateBossNameTag(player, text)
        updateBossTitle(player, text)
    }

    fun removeBossBar(player: Player) {
        if (!bossBars.containsKey(player.name)) {
            return
        }
        bossBars.remove(player.name)
        hideBossBar(player)
        val pk = RemoveEntityPacket()
        pk.eid = bossBarEid
        player.dataPacket(pk)
    }

    fun createBossEntity(player: Player, text: String?) {
        val pk = AddEntityPacket()
        pk.type = 33
        pk.entityUniqueId = bossBarEid
        pk.entityRuntimeId = bossBarEid
        pk.x = player.getX().toFloat()
        pk.y = -10.0f
        pk.z = player.getZ().toFloat()
        pk.speedX = 0f
        pk.speedY = 0f
        pk.speedZ = 0f
        pk.metadata = EntityMetadata().putLong(0, 0L).putShort(7, 400).putShort(42, 400).putLong(37, -1L).putString(4, text).putFloat(38, 0.0f)
        player.dataPacket(pk)
    }

    fun showBossBar(player: Player, text: String?) {
        val pk = BossEventPacket()
        pk.bossEid = bossBarEid
        pk.type = BossEventPacket.TYPE_SHOW
        pk.title = text
        pk.healthPercent = 1f
        pk.color = 0
        pk.overlay = 0
        player.dataPacket(pk)
    }

    private fun hideBossBar(player: Player) {
        val pk = BossEventPacket()
        pk.bossEid = bossBarEid
        pk.type = BossEventPacket.TYPE_HIDE
        player.dataPacket(pk)
    }

    private fun updateBossNameTag(player: Player, text: String) {
        val pk = SetEntityDataPacket()
        pk.eid = bossBarEid
        pk.metadata = EntityMetadata().putString(Entity.DATA_NAMETAG, text)
        player.dataPacket(pk)
    }

    private fun updateBossTitle(player: Player, text: String) {
        val pk = BossEventPacket()
        pk.bossEid = bossBarEid
        pk.type = BossEventPacket.TYPE_TITLE
        pk.healthPercent = 1f
        player.dataPacket(pk)
    }
}