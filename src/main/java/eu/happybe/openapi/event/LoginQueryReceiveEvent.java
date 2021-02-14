package eu.happybe.openapi.event;

import cn.nukkit.Player;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.player.PlayerEvent;
import eu.happybe.openapi.mysql.query.LazyRegisterQuery;
import lombok.Getter;

public class LoginQueryReceiveEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    @Getter
    private final LazyRegisterQuery query;

    public LoginQueryReceiveEvent(Player player, LazyRegisterQuery query) {
        this.player = player;
        this.query = query;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }
}
