package eu.happybe.openapi.servers;

import cn.nukkit.Player;
import lombok.Getter;
import vixikhd.portal.PortalAPI;
import vixikhd.portal.network.packets.TransferRequestPacket;

public class Server {

    @Getter
    public String serverName;
    @Getter
    public String serverAlias;
    @Getter
    public String serverAddress;
    @Getter
    public int serverPort;
    @Getter
    public int onlinePlayers;
    @Getter
    public boolean isOnline;
    @Getter
    public int whitelistState;

    public Server(String serverName, String serverAlias, String serverAddress, int serverPort) {
        this(serverName, serverAlias, serverAddress, serverPort, 0);
    }
    
    public Server(String serverName, String serverAlias, String serverAddress, int serverPort, int onlinePlayers) {
        this(serverName, serverAlias, serverAddress, serverPort, onlinePlayers, false);
    }

    public Server(String serverName, String serverAlias, String serverAddress, int serverPort, int onlinePlayers, boolean isOnline) {
        this(serverName, serverAlias, serverAddress, serverPort, onlinePlayers, isOnline, 0);
    }

    public Server(String serverName, String serverAlias, String serverAddress, int serverPort, int onlinePlayers, boolean isOnline, int whitelistState) {
        this.update(serverName, serverAlias, serverAddress, serverPort, onlinePlayers, isOnline, whitelistState);
    }

    public void update(String serverName, String serverAlias, String serverAddress, int serverPort) {
        this.update(serverName, serverAlias, serverAddress, serverPort, 0);
    }

    public void update(String serverName, String serverAlias, String serverAddress, int serverPort, int onlinePlayers) {
        this.update(serverName, serverAlias, serverAddress, serverPort, onlinePlayers, false);
    }
    
    public void update(String serverName, String serverAlias, String serverAddress, int serverPort, int onlinePlayers, boolean isOnline) {
        this.update(serverName, serverAlias, serverAddress, serverPort, onlinePlayers, isOnline, 0);
    }
    
    public void update(String serverName, String serverAlias, String serverAddress, int serverPort, int onlinePlayers, boolean isOnline, int whitelistState) {
        this.serverName = serverName;
        this.serverAlias = serverAlias;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.onlinePlayers = onlinePlayers;
        this.isOnline = isOnline;
        this.whitelistState = whitelistState;
    }
    
    public void transferPlayerHere(Player player) {
        PortalAPI.sendPacket(TransferRequestPacket.create(
            player.getUniqueId(),
            this.getServerName().substring(0, this.getServerName().indexOf("-")), // TODO - Add api method for group
            this.getServerName()
        ));
    }

    @Deprecated
    public boolean isWhitelisted() {
        return this.whitelistState != 0;
    }
}
