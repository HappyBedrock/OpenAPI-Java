package eu.happybe.openapi.mysql

import cn.nukkit.Server
import eu.happybe.openapi.OpenAPI
import java.util.*
import java.util.function.Consumer

object QueryQueue {
    private val callbacks: MutableMap<AsyncQuery, Consumer<AsyncQuery>> = HashMap()
    fun submitQuery(query: AsyncQuery) {
        submitQuery(query, null)
    }

    fun submitQuery(query: AsyncQuery, callback: Consumer<AsyncQuery>?) {
        query.host = DatabaseData.getHost()
        query.user = DatabaseData.getUser()
        query.password = DatabaseData.getPassword()
        Server.getInstance().scheduler.scheduleAsyncTask(OpenAPI.getInstance(), query)
        if (callback != null) {
            callbacks[query] = callback
        }
    }

    fun activateCallback(query: AsyncQuery) {
        if (!callbacks.containsKey(query)) {
            return
        }
        val callback = callbacks[query]!!
        callback.accept(query)
    }
}