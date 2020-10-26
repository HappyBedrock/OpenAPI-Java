package eu.happybe.openapi.servers;

import cn.nukkit.Player;
import cn.nukkit.network.protocol.ScriptCustomEventPacket;
import lombok.Getter;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Server {

    @Getter
    public String serverName;
    @Getter
    public String serverAlias;
    @Getter
    public int serverPort;
    @Getter
    public int onlinePlayers;
    @Getter
    public boolean isOnline;
    @Getter
    public boolean isWhitelisted;

    public Server(String serverName, String serverAlias, int serverPort) {
        this(serverName, serverAlias, serverPort, 0);
    }
    
    public Server(String serverName, String serverAlias, int serverPort, int onlinePlayers) {
        this(serverName, serverAlias, serverPort, onlinePlayers, false);
    }

    public Server(String serverName, String serverAlias, int serverPort, int onlinePlayers, boolean isOnline) {
        this(serverName, serverAlias, serverPort, onlinePlayers, isOnline, false);
    }

    public Server(String serverName, String serverAlias, int serverPort, int onlinePlayers, boolean isOnline, boolean isWhitelisted) {
        this.update(serverName, serverAlias, serverPort, onlinePlayers, isOnline, isWhitelisted);
    }

    public void update(String serverName, String serverAlias, int serverPort) {
        this.update(serverName, serverAlias, serverPort, 0);
    }

    public void update(String serverName, String serverAlias, int serverPort, int onlinePlayers) {
        this.update(serverName, serverAlias, serverPort, onlinePlayers, false);
    }
    
    public void update(String serverName, String serverAlias, int serverPort, int onlinePlayers, boolean isOnline) {
        this.update(serverName, serverAlias, serverPort, onlinePlayers, isOnline, false);
    }
    
    public void update(String serverName, String serverAlias, int serverPort, int onlinePlayers, boolean isOnline, boolean isWhitelisted) {
        this.serverName = serverName;
        this.serverAlias = serverAlias;
        this.serverPort = serverPort;
        this.onlinePlayers = onlinePlayers;
        this.isOnline = isOnline;
        this.isWhitelisted = isWhitelisted;
    }
    
    public void transferPlayerHere(Player player) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

            dataOutputStream.writeUTF("Connect");
            dataOutputStream.writeUTF(this.getServerName());

            ScriptCustomEventPacket pk = new ScriptCustomEventPacket();
            pk.eventName = "bungeecord:main";
            pk.eventData = outputStream.toByteArray();
            player.dataPacket(pk);
        }
        catch (IOException ignore) {}
    }
}
