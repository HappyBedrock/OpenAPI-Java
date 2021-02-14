package eu.happybe.openapi.party;

import eu.happybe.openapi.OpenAPI;
import eu.happybe.openapi.api.form.FormQueue;
import eu.happybe.openapi.api.form.types.CustomForm;
import eu.happybe.openapi.api.form.types.ModalForm;
import eu.happybe.openapi.mysql.QueryQueue;
import eu.happybe.openapi.mysql.query.*;
import eu.happybe.openapi.servers.ServerManager;
import io.gomint.GoMint;
import io.gomint.entity.EntityPlayer;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class PartyManager {

    private static final Map<String, Party> parties = new HashMap<>();
    private static final Map<String, List<EntityPlayer>> unloggedPartySessions = new HashMap<>(); // Everyone (owner too) from party whose transferred to current server, they are collected in delayed task
    private static final Map<String, BiConsumer<List<EntityPlayer>, Party>> offlineSessionHandlers = new HashMap<>();

    public static void createParty(EntityPlayer player) {
        QueryQueue.submitQuery(new FetchFriendsQuery(player.name()), query -> {
            List<String> friends = ((FetchFriendsQuery)query).friends.stream().filter(friend -> {
                EntityPlayer playerFriend = GoMint.instance().findPlayerByName(friend);
                if(playerFriend == null) {
                    return false;
                }

                return PartyManager.getPartyByPlayer(playerFriend) == null;
            }).collect(Collectors.toList());

            if(friends.size() == 0) {
                player.sendMessage("§9Parties> §cCannot create party - there aren't any online friends on the current server without a party.");
                return;
            }

            CustomForm form = new CustomForm("Invite friends to your party");
            for(String friend : friends) {
                form.addToggle(friend, false);
            }

            form.setCallable(response -> {
                if(response.getJsonData() == null) {
                    return;
                }

                System.out.println(response.getJsonData());
                Object[] data = response.getJsonData().toArray();

                QueryQueue.submitQuery(new CreatePartyQuery(player.name(), ServerManager.getCurrentServer().getServerName()));
                PartyManager.parties.put(player.name(), new Party(player));

                int i = 0, j = 0;
                for(String friend : friends) {
                    Object value = data[i];
                    if(value instanceof Boolean && (Boolean)value) {
                        EntityPlayer playerFriend = GoMint.instance().findPlayerByName(friend);
                        if(playerFriend == null) {
                            player.sendMessage("§9Parties> §6Your friend " + friend + " is no longer online.");
                            i++;
                            continue;
                        }

                        PartyManager.sendPartyInvitation(player, playerFriend);
                        i++; j++;
                    }
                }

                player.sendMessage("§9Party> §aParty created (" + j + " invitations sent)!");
            });

            FormQueue.sendForm(player, form);
        });
    }

    public static void sendPartyInvitation(EntityPlayer owner, EntityPlayer friend) {
        ModalForm form = new ModalForm("Party invitation", owner.name() + " invited you to his party!");
        form.setFirstButton("§aAccept invitation");
        form.setSecondButton("§cDecline invitation");

        form.setCallable(response -> {
            if(!owner.online()) {
                friend.sendMessage("§9Parties> §cInvitation expired.");
                return;
            }

            Party party = PartyManager.parties.getOrDefault(owner.name(), null);
            if(party == null) {
                friend.sendMessage("§9Parties> §cParty doesn't exist anymore.");
                return;
            }

            if(response.getButtonClicked() == 0) {
                party.addMember(friend);
                party.broadcastMessage("§9Party> §a" + friend.name() + " joined the party!");
            }
        });

        FormQueue.sendForm(friend, form);
    }

    public static Party getPartyByPlayer(EntityPlayer player) {
        if(PartyManager.parties.containsKey(player.name())) {
            return PartyManager.parties.get(player.name());
        }

        for(Party party : PartyManager.parties.values()) {
            if(party.containsPlayer(player)) {
                return party;
            }
        }

        return null;
    }

    public static void destroyParty(Party party) {
        QueryQueue.submitQuery(new DestroyPartyQuery(party.getOwner().name()));

        for(EntityPlayer player : party.getMembers().values()) {
            player.sendMessage("§9Party> " + party.getOwner().name() + " has destroyed his party.");
        }

        PartyManager.removeParty(party);
    }

    public static void removeParty(Party party) {
        PartyManager.parties.remove(party.getOwner().name());
    }

    public static void handleLoginQuery(EntityPlayer player, LazyRegisterQuery query) {
        if(query.parties.size() == 0) {
            return;
        }

        String owner = (String) query.parties.getOrDefault("Owner", null);
        String members = (String) query.parties.getOrDefault("Members", null);
        if(owner == null || members == null) {
            player.disconnect("Unknown party details");
            return;
        }

        List<String> membersList = members.equals("") ? new ArrayList<>() : Arrays.asList(members.split(","));

        if(PartyManager.unloggedPartySessions.containsKey(owner)) {
            PartyManager.unloggedPartySessions.get(owner).add(player);
            return;
        }

        PartyManager.unloggedPartySessions.put(owner, new ArrayList<>() {{
            this.add(player);
        }});

        OpenAPI.getInstance().scheduler().schedule(() -> {
            EntityPlayer ownerPlayer = null;

            for(EntityPlayer member : PartyManager.unloggedPartySessions.get(owner)) {
                if(member.name().equals(owner)) {
                    ownerPlayer = member;
                }
            }

            BiConsumer<List<EntityPlayer>, Party> callback = PartyManager.offlineSessionHandlers.getOrDefault(owner, null);

            if(ownerPlayer == null || (!ownerPlayer.online())) {
                QueryQueue.submitQuery(new DestroyPartyQuery(owner));
                for(EntityPlayer member : PartyManager.unloggedPartySessions.get(owner)) {
                    member.sendMessage("§9Party> §cParty destroyed (It's owner left the game)");
                }

                if(callback != null) {
                    callback.accept(unloggedPartySessions.get(owner).stream().filter(EntityPlayer::online).collect(Collectors.toList()), null);
                }

                PartyManager.unloggedPartySessions.remove(owner);
                PartyManager.offlineSessionHandlers.remove(owner);
                return;
            }

            Party party = new Party(ownerPlayer);
            Map<String, EntityPlayer> onlineMembers = new HashMap<>();
            List<String> whoseLeft = new ArrayList<>(membersList);

            for(EntityPlayer member : PartyManager.unloggedPartySessions.get(owner)) {
                if(member.online()) {
                    onlineMembers.put(member.name(), member);
                    whoseLeft.remove(member.name());
                }
            }

            for(String toRemove : whoseLeft) {
                QueryQueue.submitQuery(new RemovePartyMemberQuery(owner, toRemove));
            }
            for(EntityPlayer inGame : onlineMembers.values()) {
                party.addMember(inGame, false);
            }

            if(whoseLeft.size() > 0) {
                party.broadcastMessage("§9Party> §c" + whoseLeft.size() + " party members left the game.");
            }

            if(callback != null) {
                callback.accept((List<EntityPlayer>) party.getAll().values(), party);
            }

            QueryQueue.submitQuery(new UpdateRowQuery(new HashMap<String, Object>() {{
                this.put("CurrentServer", ServerManager.getCurrentServer().getServerName());
            }}, "Owner", owner, "Parties"));

            PartyManager.unloggedPartySessions.remove(owner);
            PartyManager.offlineSessionHandlers.remove(owner);

            PartyManager.parties.put(owner, party);
        }, 1, TimeUnit.SECONDS);
    }

    public static String isInOfflineQueue(EntityPlayer player) {
        for(String owner : PartyManager.unloggedPartySessions.keySet()) {
            for(EntityPlayer pl : PartyManager.unloggedPartySessions.get(owner)) {
                if(pl.name().equals(player.name())) {
                    return owner;
                }
            }
        }

        return null;
    }

    public static void addHandlerToOfflineSession(String owner, BiConsumer<List<EntityPlayer>, Party> callback) {
        PartyManager.offlineSessionHandlers.put(owner, callback);
    }

    public static void handleQuit(EntityPlayer player) {
        Party party = PartyManager.getPartyByPlayer(player);
        if (party == null) {
            return;
        }

        if (!party.isOnline()) {
            return;
        }

        if (party.getOwner().name().equals(player.name())) {
            party.broadcastMessage("§9Party> §cDestroying the party as it's owner (" + player.name() + ") left the game.");

            PartyManager.destroyParty(party);
            return;
        }

        party.removeMember(player);
    }
}
