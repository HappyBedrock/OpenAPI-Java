package eu.bedrockplay.openapi.servers;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

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
}
