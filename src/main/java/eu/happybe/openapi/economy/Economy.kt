package eu.happybe.openapi.economy

import cn.nukkit.Player
import eu.happybe.openapi.mysql.AsyncQuery
import eu.happybe.openapi.mysql.QueryQueue
import eu.happybe.openapi.mysql.query.AddTokensQuery
import eu.happybe.openapi.mysql.query.FetchValueQuery
import eu.happybe.openapi.mysql.query.UpdateRowQuery
import java.util.*
import java.util.function.Consumer

object Economy {
    fun addTokens(player: Player, coins: Int) {
        QueryQueue.submitQuery(AddTokensQuery(player.name, coins))
    }

    fun removeTokens(player: Player, coins: Int) {
        addTokens(player, -coins)
    }

    fun setTokens(player: Player, coins: Int) {
        QueryQueue.submitQuery(UpdateRowQuery(object : HashMap<String?, Any?>() {
            init {
                this["Tokens"] = coins.toString()
            }
        }, "Name", player.name))
    }

    fun getTokens(player: Player, callback: Consumer<Int?>) {
        QueryQueue.submitQuery(FetchValueQuery(player.name, "Tokens")) { query: AsyncQuery -> callback.accept((query as FetchValueQuery).value as Int) }
    }
}