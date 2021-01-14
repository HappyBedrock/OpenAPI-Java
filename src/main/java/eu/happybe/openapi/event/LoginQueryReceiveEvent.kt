package eu.happybe.openapi.event

import cn.nukkit.Player
import cn.nukkit.event.HandlerList
import cn.nukkit.event.player.PlayerEvent
import eu.happybe.openapi.mysql.query.LazyRegisterQuery
import lombok.Getter

class LoginQueryReceiveEvent(player: Player?, query: LazyRegisterQuery) : PlayerEvent() {
    @Getter
    private val query: LazyRegisterQuery

    companion object {
        val handlers = HandlerList()
    }

    init {
        this.player = player
        this.query = query
    }
}