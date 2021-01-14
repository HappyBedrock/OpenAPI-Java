package eu.happybe.openapi.servers

import cn.nukkit.Player
import lombok.Getter
import java.util.*
import java.util.function.Predicate
import java.util.stream.Collectors

class ServerGroup(@field:Getter private val groupName: String) {
    @Getter
    private val servers: List<Server> = ArrayList()
    fun canAddServer(server: Server): Boolean {
        return server.getServerName().contains(this.getGroupName())
    }

    fun addServer(server: Server?) {
        this.getServers().add(server)
    }

    val onlinePlayers: Int
        get() {
            var online = 0
            for (server in this.getServers()) {
                online += server.getOnlinePlayers()
            }
            return online
        }

    fun getFitServer(player: Player): Server? {
        val servers: List<Server> = this.getServers().stream()
                .filter(Predicate { server: Server -> server.getServerName() != ServerManager.getCurrentServer().getServerName() })
                .filter(Predicate { obj: Server -> obj.isOnline() })
                .filter(Predicate { server: Server -> !server.isWhitelisted() || player.hasPermission("happybe.operator") })
                .sorted(java.util.Comparator { firstServer: Server, secondServer: Server -> Integer.compare(secondServer.getOnlinePlayers(), firstServer.getOnlinePlayers()) })
                .collect(Collectors.toList())
        return if (servers.size > 0) {
            servers[0]
        } else null
    }
}