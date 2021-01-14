package eu.happybe.openapi.servers

import eu.happybe.openapi.OpenAPI
import eu.happybe.openapi.mysql.AsyncQuery
import eu.happybe.openapi.mysql.DatabaseData
import eu.happybe.openapi.mysql.QueryQueue
import eu.happybe.openapi.mysql.query.LazyRegisterServerQuery
import eu.happybe.openapi.mysql.query.ServerSyncQuery
import eu.happybe.openapi.mysql.query.UpdateRowQuery
import eu.happybe.openapi.task.ClosureTask
import lombok.Getter
import lombok.SneakyThrows
import java.sql.DriverManager
import java.util.*

object ServerManager {
    internal const val REFRESH_TICKS = 40
    private val servers: MutableMap<String?, Server?> = HashMap()
    private val serverGroups: MutableMap<String, ServerGroup?> = HashMap()

    @Getter
    private var currentServer: Server? = null
    fun init() {
        val currentServerName = OpenAPI.getInstance().config.getString("current-server-name")
        val currentServerPort = cn.nukkit.Server.getInstance().getPropertyInt("server-port")
        updateServerData(currentServerName, "null", "172.18.0.1", currentServerPort, 0, true, false)
        QueryQueue.submitQuery(LazyRegisterServerQuery(currentServerName, currentServerPort))
        currentServer = getServer(currentServerName)
        OpenAPI.getInstance().server.scheduler.scheduleRepeatingTask(ClosureTask { i: Int? ->
            QueryQueue.submitQuery(ServerSyncQuery(currentServerName, cn.nukkit.Server.getInstance().onlinePlayers.size)) { serverSyncQuery: AsyncQuery ->
                for (row in (serverSyncQuery as ServerSyncQuery).table) {
                    if (ServerManager.getCurrentServer().getServerName() == row!!["ServerName"]) {
                        continue
                    }
                    updateServerData(
                            row["ServerName"] as String?,
                            row["ServerAlias"] as String?,
                            row["ServerAddress"] as String?,
                            (row["ServerPort"] as Int?)!!,
                            (row["OnlinePlayers"] as Int?)!!,
                            (row["IsOnline"] as Boolean?)!!,
                            (row["IsWhitelisted"] as Boolean?)!!
                    )
                }
            }
        }, REFRESH_TICKS)
    }

    @SneakyThrows
    fun save() {
        val query = UpdateRowQuery(object : HashMap<String?, Any?>() {
            init {
                this["IsOnline"] = 0
                this["OnlinePlayers"] = 0
            }
        }, "ServerName", ServerManager.getCurrentServer().getServerName(), "Servers")
        val connection = DriverManager.getConnection("jdbc:mysql://" + DatabaseData.getHost() + ":3306/" + DatabaseData.DATABASE, DatabaseData.getUser(), DatabaseData.getPassword())
        query.query(connection.createStatement())
        connection.close()
    }

    fun updateServerData(serverName: String?, serverAlias: String?, serverAddress: String?, serverPort: Int, onlinePlayers: Int, isOnline: Boolean, isWhitelisted: Boolean) {
        if (!servers.containsKey(serverName)) {
            val server = Server(serverName, serverAlias, serverAddress, serverPort, onlinePlayers, isOnline, isWhitelisted)
            servers[serverName] = server
            OpenAPI.getInstance().logger.info("§aRegistered new server ($serverName)")
            val groupName = serverName!!.substring(0, serverName.indexOf("-"))
            val targetGroup = getServerGroup(groupName)
            if (targetGroup == null) {
                val group = ServerGroup(groupName)
                serverGroups[groupName] = group
                group.addServer(server)
                return
            }
            targetGroup.addServer(server)
            return
        }
        servers[serverName]!!.update(serverName, serverAlias, serverAddress, serverPort, onlinePlayers, isOnline, isWhitelisted)
    }

    fun getServerGroup(name: String): ServerGroup? {
        return serverGroups.getOrDefault(name, null)
    }

    fun getServer(name: String?): Server? {
        return servers.getOrDefault(name, null)
    }
}