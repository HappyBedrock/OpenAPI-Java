package eu.happybe.openapi.servers;

import cn.nukkit.Player;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ServerGroup {

    @Getter
    private final String groupName;
    @Getter
    private final List<Server> servers = new ArrayList<>();

    public ServerGroup(String groupName) {
        this.groupName = groupName;
    }

    public boolean canAddServer(Server server) {
        return server.getServerName().contains(this.getGroupName());
    }

    public void addServer(Server server) {
        this.getServers().add(server);
    }

    public int getOnlinePlayers() {
        int online = 0;
        for(Server server : this.getServers()) {
            online += server.getOnlinePlayers();
        }

        return online;
    }

    public Server getFitServer(Player player) {
        List<Server> servers = this.getServers().stream()
                .filter(server -> !server.getServerName().equals(ServerManager.getCurrentServer().getServerName()))
                .filter(Server::isOnline)
                .filter(server -> (!server.isWhitelisted()) || player.hasPermission("happybe.operator"))
                .sorted((firstServer, secondServer) -> Integer.compare(secondServer.getOnlinePlayers(), firstServer.getOnlinePlayers()))
                .collect(Collectors.toList());

        if(servers.size() > 0) {
            return servers.get(0);
        }

        return null;
    }
}
