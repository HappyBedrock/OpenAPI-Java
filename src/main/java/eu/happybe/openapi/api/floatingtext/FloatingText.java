package eu.happybe.openapi.api.floatingtext;

import io.gomint.GoMint;
import io.gomint.entity.EntityPlayer;
import io.gomint.entity.passive.EntityHuman;
import io.gomint.math.Location;
import io.gomint.math.Vector;
import lombok.AccessLevel;
import lombok.Getter;

public class FloatingText {

    @Getter(AccessLevel.PRIVATE)
    private final Vector position;
    @Getter(AccessLevel.PRIVATE)
    private final EntityHuman entity;

    public FloatingText(Vector position) {
        this.position = position;
        this.entity = EntityHuman.create();

        this.getEntity().scale(0);
        this.getEntity().skin(GoMint.instance().emptyPlayerSkin());
        this.getEntity().hiddenByDefault(true);
    }

    public void spawnTo(EntityPlayer player, String text) {
        this.getEntity().nameTag(text);
        this.getEntity().showFor(player);
        this.getEntity().spawn(new Location(player.world(), this.getPosition()));
    }

    public void despawnFrom(EntityPlayer player) {
        this.getEntity().hideFor(player);
        // TODO - Destroy human with floating text
    }
}
