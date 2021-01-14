package eu.happybe.openapi.servers

import cn.nukkit.Player
import cn.nukkit.network.protocol.TransferPacket
import lombok.Getter

class Server @JvmOverloads constructor(serverName: String?, serverAlias: String?, serverAddress: String?, serverPort: Int, onlinePlayers: Int = 0, isOnline: Boolean = false, isWhitelisted: Boolean = false) {
    @Getter
    var serverName: String? = null

    @Getter
    var serverAlias: String? = null

    @Getter
    var serverAddress: String? = null

    @Getter
    var serverPort = 0

    @Getter
    var onlinePlayers = 0

    @Getter
    var isOnline = false

    @Getter
    var isWhitelisted = false
    @JvmOverloads
    fun update(serverName: String?, serverAlias: String?, serverAddress: String?, serverPort: Int, onlinePlayers: Int = 0, isOnline: Boolean = false, isWhitelisted: Boolean = false) {
        this.serverName = serverName
        this.serverAlias = serverAlias
        this.serverAddress = serverAddress
        this.serverPort = serverPort
        this.onlinePlayers = onlinePlayers
        this.isOnline = isOnline
        this.isWhitelisted = isWhitelisted
    }

    fun transferPlayerHere(player: Player) {
        val pk = TransferPacket()
        pk.address = this.getServerAddress()
        pk.port = this.getServerPort()
        player.dataPacket(pk)
    }

    init {
        update(serverName, serverAlias, serverAddress, serverPort, onlinePlayers, isOnline, isWhitelisted)
    }
}