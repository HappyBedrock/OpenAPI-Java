package eu.happybe.openapi.friends

import cn.nukkit.Player
import cn.nukkit.Server
import eu.happybe.openapi.form.FormCallbackResponse
import eu.happybe.openapi.form.FormQueue
import eu.happybe.openapi.form.types.ModalForm
import eu.happybe.openapi.mysql.AsyncQuery
import eu.happybe.openapi.mysql.QueryQueue
import eu.happybe.openapi.mysql.query.AddFriendQuery
import java.util.function.BiConsumer

object FriendsManager {
    fun setFriends(player: Player, friend: Player) {
        setFriends(player, friend, null)
    }

    fun setFriends(player: Player, friend: Player, callback: BiConsumer<Player?, AddFriendQuery?>?) {
        QueryQueue.submitQuery(AddFriendQuery(player.name, friend.name)) { addFriendsQuery: AsyncQuery? -> callback?.accept(player, addFriendsQuery as AddFriendQuery?) }
    }

    // TODO - Move this to BasicEssentials
    fun sendFriendRequest(player: Player, newFriend: Player) {
        val form = ModalForm("Friend Request", player.name + " sent you a friend request.")
        form.setFirstButton("§aAccept")
        form.setSecondButton("§cDecline")
        form.customData = player.name
        form.setCallable { response: FormCallbackResponse? ->
            val friend = response.getPlayer()
            if (response.getButtonClicked() == 1) {
                friend.sendMessage("§9Friends> §cFriend request cancelled.")
                return@setCallable
            }
            val sender = Server.getInstance().getPlayerExact(response.getForm().customData as String)
            if (sender == null || !sender.isOnline) {
                friend.sendMessage("§9Friends> §cFriend request expired.")
                return@setCallable
            }
            sender.sendMessage("§9Friends> §a$friend accepted your friend request.")
            friend.sendMessage("§9Friends> §aFriend request accepted!")
            setFriends(sender, friend)
        }
        FormQueue.sendForm(newFriend, form)
    }
}