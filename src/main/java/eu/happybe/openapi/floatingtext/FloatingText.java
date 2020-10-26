package eu.happybe.openapi.floatingtext;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.EntityMetadata;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.AddPlayerPacket;
import cn.nukkit.network.protocol.RemoveEntityPacket;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.UUID;

public class FloatingText {

    @Getter
    private final Vector3 position;
    @Getter
    private final long entityRuntimeId;

    @Getter(AccessLevel.PRIVATE)
    private final AddPlayerPacket packet = new AddPlayerPacket();

    public FloatingText(Vector3 position) {
        this.position = position;
        this.entityRuntimeId = Entity.entityCount++;

        this.packet.uuid = UUID.randomUUID();
        this.packet.entityRuntimeId = this.getEntityRuntimeId();
        this.packet.entityUniqueId = this.getEntityRuntimeId();
        this.packet.x = (float) this.getPosition().getX();
        this.packet.y = (float) this.getPosition().getY();
        this.packet.z = (float) this.getPosition().getZ();
        this.packet.item = Item.get(0);
        this.packet.metadata = new EntityMetadata().putLong(Entity.DATA_FLAGS, 1 << Entity.DATA_FLAG_IMMOBILE).putFloat(Entity.DATA_SCALE, 0F);
    }

    public void spawnTo(Player player, String text) {
        AddPlayerPacket pk = this.getPacket();
        pk.username = text;
        player.dataPacket(pk);
    }

    public void despawnFrom(Player player) {
        RemoveEntityPacket pk = new RemoveEntityPacket();
        pk.eid = this.getEntityRuntimeId();
        player.dataPacket(pk);
    }
}
