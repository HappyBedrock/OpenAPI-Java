package eu.happybe.openapi.party

import cn.nukkit.Player
import eu.happybe.openapi.mysql.QueryQueue
import eu.happybe.openapi.mysql.query.AddPartyMemberQuery
import eu.happybe.openapi.mysql.query.RemovePartyMemberQuery
import eu.happybe.openapi.mysql.query.UpdateRowQuery
import eu.happybe.openapi.servers.Server
import eu.happybe.openapi.servers.ServerManager
import lombok.Getter
import java.util.*

class Party(@field:Getter private val owner: Player) {
    @Getter
    var isOnline = true

    @Getter
    private val members: Map<String, Player> = HashMap()
    @JvmOverloads
    fun addMember(player: Player, updateInDatabase: Boolean = true) {
        if (updateInDatabase) {
            QueryQueue.submitQuery(AddPartyMemberQuery(this.getOwner().getName(), player.name))
        }
        this.getMembers().put(player.name, player)
    }

    @JvmOverloads
    fun removeMember(player: Player, updateInDatabase: Boolean = true) {
        if (updateInDatabase) {
            QueryQueue.submitQuery(RemovePartyMemberQuery(this.getOwner().getName(), player.name))
        }
        this.getMembers().remove(player.name)
    }

    fun containsPlayer(player: Player): Boolean {
        return this.getMembers().containsKey(player.name)
    }

    fun broadcastMessage(message: String?) {
        for (player in all.values) {
            player.sendMessage(message)
        }
    }

    fun transfer(server: Server) {
        QueryQueue.submitQuery(UpdateRowQuery(object : HashMap<String?, Any?>() {
            init {
                this["CurrentServer"] = server.getServerName()
            }
        }, "Owner", this.getOwner().getName(), "Parties"))
        isOnline = server.getServerName() == ServerManager.getCurrentServer().getServerName()
        server.transferPlayerHere(this.getOwner())
        for (member in this.getMembers().values) {
            server.transferPlayerHere(member)
        }
        if (!this.isOnline()) {
            PartyManager.removeParty(this)
        }
    }

    val all: Map<String, Player>
        get() {
            val all: MutableMap<String, Player> = HashMap<String, Player>(this.getMembers())
            all[this.getOwner().getName()] = this.getOwner()
            return all
        }
}