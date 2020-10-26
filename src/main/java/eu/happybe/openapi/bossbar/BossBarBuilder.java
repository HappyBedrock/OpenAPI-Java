package eu.happybe.openapi.bossbar;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.EntityMetadata;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.network.protocol.BossEventPacket;
import cn.nukkit.network.protocol.RemoveEntityPacket;
import cn.nukkit.network.protocol.SetEntityDataPacket;

import java.util.HashMap;
import java.util.Map;

public class BossBarBuilder {

    private static long bossBarEid = -1;
    private static final Map<String, String> bossBars = new HashMap<>();

    public static void sendBossBarText(Player player, String text) {
        if(!BossBarBuilder.bossBars.containsKey(player.getName())) {
            BossBarBuilder.bossBars.put(player.getName(), text);
            BossBarBuilder.createBossEntity(player, text);
            BossBarBuilder.showBossBar(player, text);
            return;
        }

        if(BossBarBuilder.bossBars.get(player.getName()).equals(text)) {
            return;
        }

        BossBarBuilder.updateBossNameTag(player, text);
        BossBarBuilder.updateBossTitle(player, text);
    }

    public static void removeBossBar(Player player) {
        if(!BossBarBuilder.bossBars.containsKey(player.getName())) {
            return;
        }
        BossBarBuilder.bossBars.remove(player.getName());

        BossBarBuilder.hideBossBar(player);

        RemoveEntityPacket pk = new RemoveEntityPacket();
        pk.eid = BossBarBuilder.getBossBarEid();
        player.dataPacket(pk);
    }

    public static void createBossEntity(Player player, String text) {
        AddEntityPacket pk = new AddEntityPacket();
        pk.type = 33;
        pk.entityUniqueId = BossBarBuilder.getBossBarEid();
        pk.entityRuntimeId = BossBarBuilder.getBossBarEid();
        pk.x = (float)player.getX();
        pk.y = -10.0F;
        pk.z = (float)player.getZ();
        pk.speedX = 0F;
        pk.speedY = 0F;
        pk.speedZ = 0F;
        pk.metadata = (new EntityMetadata()).putLong(0, 0L).putShort(7, 400).putShort(42, 400).putLong(37, -1L).putString(4, text).putFloat(38, 0.0F);

        player.dataPacket(pk);
    }

    public static void showBossBar(Player player, String text) {
        BossEventPacket pk = new BossEventPacket();
        pk.bossEid = BossBarBuilder.getBossBarEid();
        pk.type = BossEventPacket.TYPE_SHOW;
        pk.title = text;
        pk.healthPercent = 1F;
        pk.color = 0;
        pk.overlay = 0;

        player.dataPacket(pk);
    }

    private static void hideBossBar(Player player) {
        BossEventPacket pk = new BossEventPacket();
        pk.bossEid = BossBarBuilder.getBossBarEid();
        pk.type = BossEventPacket.TYPE_HIDE;

        player.dataPacket(pk);
    }

    private static void updateBossNameTag(Player player, String text) {
        SetEntityDataPacket pk = new SetEntityDataPacket();
        pk.eid = BossBarBuilder.getBossBarEid();
        pk.metadata = new EntityMetadata().putString(Entity.DATA_NAMETAG, text);

        player.dataPacket(pk);
    }

    private static void updateBossTitle(Player player, String text) {
        BossEventPacket pk = new BossEventPacket();
        pk.bossEid = BossBarBuilder.getBossBarEid();
        pk.type = BossEventPacket.TYPE_TITLE;
        pk.healthPercent = 1;

        player.dataPacket(pk);
    }

    private static long getBossBarEid() {
        if(BossBarBuilder.bossBarEid == -1) {
            BossBarBuilder.bossBarEid = Entity.entityCount++;
        }

        return BossBarBuilder.bossBarEid;
    }
}
