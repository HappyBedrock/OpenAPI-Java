package eu.happybe.openapi.party;

import cn.nukkit.Player;
import eu.happybe.openapi.mysql.QueryQueue;
import eu.happybe.openapi.mysql.query.AddPartyMemberQuery;
import eu.happybe.openapi.mysql.query.RemovePartyMemberQuery;
import eu.happybe.openapi.mysql.query.UpdateRowQuery;
import eu.happybe.openapi.servers.Server;
import eu.happybe.openapi.servers.ServerManager;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class Party {

    @Getter
    private boolean isOnline = true;
    @Getter
    private final Player owner;

    @Getter
    private final Map<String, Player> members = new HashMap<>();

    public Party(Player owner) {
        this.owner = owner;
    }

    public void addMember(Player player) {
        this.addMember(player, true);
    }

    public void addMember(Player player, boolean updateInDatabase) {
        if(updateInDatabase) {
            QueryQueue.submitQuery(new AddPartyMemberQuery(this.getOwner().getName(), player.getName()));
        }

        this.getMembers().put(player.getName(), player);
    }

    public void removeMember(Player player) {
        this.removeMember(player, true);
    }

    public void removeMember(Player player, boolean updateInDatabase) {
        if(updateInDatabase) {
            QueryQueue.submitQuery(new RemovePartyMemberQuery(this.getOwner().getName(), player.getName()));
        }

        this.getMembers().remove(player.getName());
    }

    public boolean containsPlayer(Player player) {
        return this.getMembers().containsKey(player.getName());
    }

    public void broadcastMessage(String message) {
        for(Player player : this.getAll().values()) {
            player.sendMessage(message);
        }
    }

    public void transfer(Server server) {
        QueryQueue.submitQuery(new UpdateRowQuery(new HashMap<String, Object> () {{
            this.put("CurrentServer", server.getServerName());
        }}, "Owner", this.getOwner().getName(), "Parties"));

        this.isOnline = server.getServerName().equals(ServerManager.getCurrentServer().getServerName());

        server.transferPlayerHere(this.getOwner());
        for(Player member : this.getMembers().values()) {
            server.transferPlayerHere(member);
        }

        if(!this.isOnline()) {
            PartyManager.removeParty(this);
        }
    }

    public Map<String, Player> getAll() {
        Map<String, Player> all = new HashMap<>(this.getMembers());
        all.put(this.getOwner().getName(), this.getOwner());

        return all;
    }
}