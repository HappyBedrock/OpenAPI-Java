package eu.happybe.openapi.friends;

import eu.happybe.openapi.api.form.FormQueue;
import eu.happybe.openapi.api.form.types.ModalForm;
import eu.happybe.openapi.mysql.QueryQueue;
import eu.happybe.openapi.mysql.query.AddFriendQuery;
import io.gomint.GoMint;
import io.gomint.entity.EntityPlayer;

import java.util.function.BiConsumer;

public class FriendsManager {

    public static void setFriends(EntityPlayer player, EntityPlayer friend) {
        FriendsManager.setFriends(player, friend, null);
    }

    public static void setFriends(EntityPlayer player, EntityPlayer friend, BiConsumer<EntityPlayer, AddFriendQuery> callback) {
        QueryQueue.submitQuery(new AddFriendQuery(player.name(), friend.name()), addFriendsQuery -> {
            if(callback != null) {
                callback.accept(player, (AddFriendQuery) addFriendsQuery);
            }
        });
    }

    // TODO - Move this to BasicEssentials
    public static void sendFriendRequest(EntityPlayer player, EntityPlayer newFriend) {
        ModalForm form = new ModalForm("Friend Request", player.name() + " sent you a friend request.");
        form.setFirstButton("§aAccept");
        form.setSecondButton("§cDecline");

        form.setCustomData(player.name());

        form.setCallable(response -> {
            EntityPlayer friend = response.getPlayer();
            if(response.getButtonClicked() == 1) {
                friend.sendMessage("§9Friends> §cFriend request cancelled.");
                return;
            }

            EntityPlayer sender = GoMint.instance().findPlayerByName((String)response.getForm().getCustomData());
//            EntityPlayer sender = Server.getInstance().getEntityPlayerExact((String) response.getForm().getCustomData());
            if(sender == null || !sender.online()) {
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
