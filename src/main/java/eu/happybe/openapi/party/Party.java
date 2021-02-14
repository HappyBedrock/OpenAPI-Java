package eu.happybe.openapi.party;

import eu.happybe.openapi.mysql.QueryQueue;
import eu.happybe.openapi.mysql.query.AddPartyMemberQuery;
import eu.happybe.openapi.mysql.query.RemovePartyMemberQuery;
import eu.happybe.openapi.mysql.query.UpdateRowQuery;
import eu.happybe.openapi.servers.Server;
import eu.happybe.openapi.servers.ServerManager;
import io.gomint.entity.EntityPlayer;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class Party {

    @Getter
    private boolean isOnline = true;
    @Getter
    private final EntityPlayer owner;

    @Getter
    private final Map<String, EntityPlayer> members = new HashMap<>();

    public Party(EntityPlayer owner) {
        this.owner = owner;
    }

    public void addMember(EntityPlayer player) {
        this.addMember(player, true);
    }

    public void addMember(EntityPlayer player, boolean updateInDatabase) {
        if(updateInDatabase) {
            QueryQueue.submitQuery(new AddPartyMemberQuery(this.getOwner().name(), player.name()));
        }

        this.getMembers().put(player.name(), player);
    }

    public void removeMember(EntityPlayer player) {
        this.removeMember(player, true);
    }

    public void removeMember(EntityPlayer player, boolean updateInDatabase) {
        if(updateInDatabase) {
            QueryQueue.submitQuery(new RemovePartyMemberQuery(this.getOwner().name(), player.name()));
        }

        this.getMembers().remove(player.name());
    }

    public boolean containsPlayer(EntityPlayer player) {
        return this.getMembers().containsKey(player.name());
    }

    public void broadcastMessage(String message) {
        for(EntityPlayer player : this.getAll().values()) {
            player.sendMessage(message);
        }
    }

    public void transfer(Server server) {
        QueryQueue.submitQuery(new UpdateRowQuery(new HashMap<String, Object> () {{
            this.put("CurrentServer", server.getServerName());
        }}, "Owner", this.getOwner().name(), "Parties"));

        this.isOnline = server.getServerName().equals(ServerManager.getCurrentServer().getServerName());

        server.transferPlayerHere(this.getOwner());
        for(EntityPlayer member : this.getMembers().values()) {
            server.transferPlayerHere(member);
        }

        if(!this.isOnline()) {
            PartyManager.removeParty(this);
        }
    }

    public Map<String, EntityPlayer> getAll() {
        Map<String, EntityPlayer> all = new HashMap<>(this.getMembers());
        all.put(this.getOwner().name(), this.getOwner());

        return all;
    }
}