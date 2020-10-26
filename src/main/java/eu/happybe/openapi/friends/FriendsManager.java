package eu.happybe.openapi.friends;

import cn.nukkit.Player;
import cn.nukkit.Server;
import eu.happybe.openapi.form.FormQueue;
import eu.happybe.openapi.form.types.ModalForm;
import eu.happybe.openapi.mysql.QueryQueue;
import eu.happybe.openapi.mysql.query.AddFriendQuery;

import java.util.function.BiConsumer;

public class FriendsManager {

    public static void setFriends(Player player, Player friend) {
        FriendsManager.setFriends(player, friend, null);
    }

    public static void setFriends(Player player, Player friend, BiConsumer<Player, AddFriendQuery> callback) {
        QueryQueue.submitQuery(new AddFriendQuery(player.getName(), friend.getName()), addFriendsQuery -> {
            if(callback != null) {
                callback.accept(player, (AddFriendQuery) addFriendsQuery);
            }
        });
    }

    // TODO - Move this to BasicEssentials
    public static void sendFriendRequest(Player player, Player newFriend) {
        ModalForm form = new ModalForm("Friend Request", player.getName() + " sent you a friend request.");
        form.setFirstButton("§aAccept");
        form.setSecondButton("§cDecline");

        form.setCustomData(player.getName());

        form.setCallable(response -> {
            Player friend = response.getPlayer();
            if(response.getButtonClicked() == 1) {
                friend.sendMessage("§9Friends> §cFriend request cancelled.");
                return;
            }

            Player sender = Server.getInstance().getPlayerExact((String) response.getForm().getCustomData());
            if(sender == null || !sender.isOnline()) {
                friend.sendMessage("§9Friends> §cFriend request expired.");
                return;
            }

            sender.sendMessage("§9Friends> §a" + friend + " accepted your friend request.");
            friend.sendMessage("§9Friends> §aFriend request accepted!");

            FriendsManager.setFriends(sender, friend);
        });

        FormQueue.sendForm(newFriend, form);
    }
}
