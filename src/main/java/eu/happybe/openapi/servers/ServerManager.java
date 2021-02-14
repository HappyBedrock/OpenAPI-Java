package eu.happybe.openapi.servers;

import eu.happybe.openapi.OpenAPI;
import eu.happybe.openapi.mysql.DatabaseData;
import eu.happybe.openapi.mysql.QueryQueue;
import eu.happybe.openapi.mysql.query.LazyRegisterServerQuery;
import eu.happybe.openapi.mysql.query.ServerSyncQuery;
import eu.happybe.openapi.mysql.query.UpdateRowQuery;
import eu.happybe.openapi.task.ClosureTask;
import lombok.Getter;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;

public class ServerManager {

    protected static final int REFRESH_TICKS = 40;

    private static final Map<String, Server> servers = new HashMap<>();
    private static final Map<String, ServerGroup> serverGroups = new HashMap<>();

    @Getter
    private static Server currentServer;

    public static void init() {
        String currentServerName = OpenAPI.getInstance().getConfig().getString("current-server-name");
        int currentServerPort = cn.nukkit.Server.getInstance().getPropertyInt("server-port");

        ServerManager.updateServerData(currentServerName, "null", "172.18.0.1", currentServerPort, 0, true, false);
        QueryQueue.submitQuery(new LazyRegisterServerQuery(currentServerName, currentServerPort));

        ServerManager.currentServer = ServerManager.getServer(currentServerName);

        OpenAPI.getInstance().getServer().getScheduler().scheduleRepeatingTask(new ClosureTask((i) -> {
            QueryQueue.submitQuery(new ServerSyncQuery(currentServerName, cn.nukkit.Server.getInstance().getOnlinePlayers().size()), (serverSyncQuery) -> {
                for(Map<String, Object> row : ((ServerSyncQuery)serverSyncQuery).table) {
                    if(ServerManager.getCurrentServer().getServerName().equals(row.get("ServerName"))) {
                        continue;
                    }

                    ServerManager.updateServerData(
                        (String) row.get("ServerName"),
                        (String) row.get("ServerAlias"),
                        (String) row.get("ServerAddress"),
                        (Integer) row.get("ServerPort"),
                        (Integer) row.get("OnlinePlayers"),
                        (Boolean) row.get("IsOnline"),
                        (Boolean) row.get("IsWhitelisted")
                    );
                }
            });
        }), ServerManager.REFRESH_TICKS);
    }

    @SneakyThrows
    public static void save() {
        UpdateRowQuery query = new UpdateRowQuery(new HashMap<String, Object>() {{
            this.put("IsOnline", 0);
            this.put("OnlinePlayers", 0);
        }}, "ServerName", ServerManager.getCurrentServer().getServerName(), "Servers");

        Connection connection = DriverManager.getConnection("jdbc:mysql://" + DatabaseData.getHost() + ":3306/" + DatabaseData.DATABASE, DatabaseData.getUser(), DatabaseData.getPassword());
        query.query(connection.createStatement());
        connection.close();
    }

    public static void updateServerData(String serverName, String serverAlias, String serverAddress, int serverPort, int onlinePlayers, boolean isOnline, boolean isWhitelisted) {
        if(!ServerManager.servers.containsKey(serverName)) {
            Server server = new Server(serverName, serverAlias, serverAddress, serverPort, onlinePlayers, isOnline, isWhitelisted);
            ServerManager.servers.put(serverName, server);
            OpenAPI.getInstance().getLogger().info("§aRegistered new server (" + serverName + ")");

            String groupName = serverName.substring(0, serverName.indexOf("-"));
            ServerGroup targetGroup = getServerGroup(groupName);

            if(targetGroup == null) {
                ServerGroup group = new ServerGroup(groupName);
                ServerManager.serverGroups.put(groupName, group);

                group.addServer(server);
                return;
            }

            targetGroup.addServer(server);
            return;
        }

        ServerManager.servers.get(serverName).update(serverName, serverAlias, serverAddress, serverPort, onlinePlayers, isOnline, isWhitelisted);
    }

    public static ServerGroup getServerGroup(String name) {
        return ServerManager.serverGroups.getOrDefault(name, null);
    }

    public static Server getServer(String name) {
        return ServerManager.servers.getOrDefault(name, null);
    }
}
