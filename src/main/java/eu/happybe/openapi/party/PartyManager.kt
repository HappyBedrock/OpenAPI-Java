package eu.happybe.openapi.party

import cn.nukkit.Player
import cn.nukkit.Server
import eu.happybe.openapi.OpenAPI
import eu.happybe.openapi.form.FormCallbackResponse
import eu.happybe.openapi.form.FormQueue
import eu.happybe.openapi.form.types.CustomForm
import eu.happybe.openapi.form.types.ModalForm
import eu.happybe.openapi.mysql.AsyncQuery
import eu.happybe.openapi.mysql.QueryQueue
import eu.happybe.openapi.mysql.query.*
import eu.happybe.openapi.servers.ServerManager
import eu.happybe.openapi.task.ClosureTask
import java.util.*
import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.stream.Collectors

object PartyManager {
    private val parties: MutableMap<String, Party?> = HashMap()
    private val unloggedPartySessions: MutableMap<String, MutableList<Player>> = HashMap() // Everyone (owner too) from party whose transferred to current server, they are collected in delayed task
    private val offlineSessionHandlers: MutableMap<String, BiConsumer<List<Player>, Party?>?> = HashMap()
    fun createParty(player: Player) {
        QueryQueue.submitQuery(FetchFriendsQuery(player.name)) { query: AsyncQuery ->
            val friends = (query as FetchFriendsQuery).friends.stream().filter { friend: String? ->
                val playerFriend = Server.getInstance().getPlayerExact(friend) ?: return@filter false
                getPartyByPlayer(playerFriend) == null
            }.collect(Collectors.toList())
            if (friends.size == 0) {
                player.sendMessage("§9Parties> §cCannot create party - there aren't any online friends on the current server without a party.")
                return@submitQuery
            }
            val form = CustomForm("Invite friends to your party")
            for (friend in friends) {
                form.addToggle(friend, false)
            }
            form.setCallable { response: FormCallbackResponse? ->
                if (response.getJsonData() == null) {
                    return@setCallable
                }
                println(response.getJsonData())
                val data: Array<Any> = response.getJsonData().toTypedArray()
                QueryQueue.submitQuery(CreatePartyQuery(player.name, ServerManager.getCurrentServer().getServerName()))
                parties[player.name] = Party(player)
                var i = 0
                var j = 0
                for (friend in friends) {
                    val value = data[i]
                    if (value is Boolean && value.toBoolean()) {
                        val playerFriend = Server.getInstance().getPlayerExact(friend)
                        if (playerFriend == null) {
                            player.sendMessage("§9Parties> §6Your friend $friend is no longer online.")
                            i++
                            continue
                        }
                        sendPartyInvitation(player, playerFriend)
                        i++
                        j++
                    }
                }
                player.sendMessage("§9Party> §aParty created ($j invitations sent)!")
            }
            FormQueue.sendForm(player, form)
        }
    }

    fun sendPartyInvitation(owner: Player, friend: Player) {
        val form = ModalForm("Party invitation", owner.name + " invited you to his party!")
        form.setFirstButton("§aAccept invitation")
        form.setSecondButton("§cDecline invitation")
        form.setCallable { response: FormCallbackResponse? ->
            if (!owner.isOnline) {
                friend.sendMessage("§9Parties> §cInvitation expired.")
                return@setCallable
            }
            val party = parties.getOrDefault(owner.name, null)
            if (party == null) {
                friend.sendMessage("§9Parties> §cParty doesn't exist anymore.")
                return@setCallable
            }
            if (response.getButtonClicked() == 0) {
                party.addMember(friend)
                party.broadcastMessage("§9Party> §a" + friend.name + " joined the party!")
            }
        }
        FormQueue.sendForm(friend, form)
    }

    fun getPartyByPlayer(player: Player): Party? {
        if (parties.containsKey(player.name)) {
            return parties[player.name]
        }
        for (party in parties.values) {
            if (party!!.containsPlayer(player)) {
                return party
            }
        }
        return null
    }

    fun destroyParty(party: Party) {
        QueryQueue.submitQuery(DestroyPartyQuery(party.owner.name))
        for (player in party.members.values) {
            player.sendMessage("§9Party> " + party.owner.name + " has destroyed his party.")
        }
        removeParty(party)
    }

    fun removeParty(party: Party) {
        parties.remove(party.owner.name)
    }

    fun handleLoginQuery(player: Player, query: LazyRegisterQuery) {
        if (query.parties.size == 0) {
            return
        }
        val owner = query.parties.getOrDefault("Owner", null) as String
        val members = query.parties.getOrDefault("Members", null) as String
        if (owner == null || members == null) {
            player.kick("Unknown party details")
            return
        }
        val membersList = if (members == "") ArrayList() else Arrays.asList(*members.split(",").toTypedArray())
        if (unloggedPartySessions.containsKey(owner)) {
            unloggedPartySessions[owner]!!.add(player)
            return
        }
        unloggedPartySessions[owner] = object : ArrayList<Player?>() {
            init {
                this.add(player)
            }
        }
        OpenAPI.getInstance().server.scheduler.scheduleDelayedTask(ClosureTask(label@ Consumer { i: Int? ->
            var ownerPlayer: Player? = null
            for (member in unloggedPartySessions[owner]!!) {
                if (member.name == owner) {
                    ownerPlayer = member
                }
            }
            val callback = offlineSessionHandlers.getOrDefault(owner, null)
            if (ownerPlayer == null || !ownerPlayer.isOnline) {
                QueryQueue.submitQuery(DestroyPartyQuery(owner))
                for (member in unloggedPartySessions[owner]!!) {
                    member.sendMessage("§9Party> §cParty destroyed (It's owner left the game)")
                }
                callback?.accept(unloggedPartySessions[owner]!!.stream().filter { obj: Player -> obj.isOnline }.collect(Collectors.toList()), null)
                unloggedPartySessions.remove(owner)
                offlineSessionHandlers.remove(owner)
                return@label
            }
            val party = Party(ownerPlayer)
            val onlineMembers: MutableMap<String, Player> = HashMap()
            val whoseLeft: MutableList<String> = ArrayList(membersList)
            for (member in unloggedPartySessions[owner]!!) {
                if (member.isOnline) {
                    onlineMembers[member.name] = member
                    whoseLeft.remove(member.name)
                }
            }
            for (toRemove in whoseLeft) {
                QueryQueue.submitQuery(RemovePartyMemberQuery(owner, toRemove))
            }
            for (inGame in onlineMembers.values) {
                party.addMember(inGame, false)
            }
            if (whoseLeft.size > 0) {
                party.broadcastMessage("§9Party> §c" + whoseLeft.size + " party members left the game.")
            }
            callback?.accept(ArrayList(party.all.values), party)
            QueryQueue.submitQuery(UpdateRowQuery(object : HashMap<String?, Any?>() {
                init {
                    this["CurrentServer"] = ServerManager.getCurrentServer().getServerName()
                }
            }, "Owner", owner, "Parties"))
            unloggedPartySessions.remove(owner)
            offlineSessionHandlers.remove(owner)
            parties[owner] = party
        }), 20)
    }

    fun isInOfflineQueue(player: Player): String? {
        for (owner in unloggedPartySessions.keys) {
            for (pl in unloggedPartySessions[owner]!!) {
                if (pl.name == player.name) {
                    return owner
                }
            }
        }
        return null
    }

    fun addHandlerToOfflineSession(owner: String, callback: BiConsumer<List<Player>, Party?>?) {
        offlineSessionHandlers[owner] = callback
    }

    fun handleQuit(player: Player) {
        val party = getPartyByPlayer(player) ?: return
        if (!party.isOnline) {
            return
        }
        if (party.owner.name == player.name) {
            party.broadcastMessage("§9Party> §cDestroying the party as it's owner (" + player.name + ") left the game.")
            destroyParty(party)
            return
        }
        party.removeMember(player)
    }
}