package eu.happybe.openapi.floatingtext

import cn.nukkit.Player
import cn.nukkit.entity.Entity
import cn.nukkit.entity.data.EntityMetadata
import cn.nukkit.item.Item
import cn.nukkit.math.Vector3
import cn.nukkit.network.protocol.AddPlayerPacket
import cn.nukkit.network.protocol.RemoveEntityPacket
import lombok.AccessLevel
import lombok.Getter
import java.util.*

class FloatingText(@field:Getter private val position: Vector3) {
    @Getter
    private val entityRuntimeId: Long

    @Getter(AccessLevel.PRIVATE)
    private val packet = AddPlayerPacket()
    fun spawnTo(player: Player, text: String?) {
        val pk: AddPlayerPacket = this.getPacket()
        pk.username = text
        player.dataPacket(pk)
    }

    fun despawnFrom(player: Player) {
        val pk = RemoveEntityPacket()
        pk.eid = this.getEntityRuntimeId()
        player.dataPacket(pk)
    }

    init {
        entityRuntimeId = Entity.entityCount++
        packet.uuid = UUID.randomUUID()
        packet.entityRuntimeId = this.getEntityRuntimeId()
        packet.entityUniqueId = this.getEntityRuntimeId()
        packet.x = this.getPosition().getX()
        packet.y = this.getPosition().getY()
        packet.z = this.getPosition().getZ()
        packet.item = Item.get(0)
        packet.metadata = EntityMetadata().putLong(Entity.DATA_FLAGS, (1 shl Entity.DATA_FLAG_IMMOBILE.toLong().toInt()).toLong()).putFloat(Entity.DATA_SCALE, 0f)
    }
}