package eu.happybe.openapi.party;

import cn.nukkit.Player;
import cn.nukkit.Server;
import eu.happybe.openapi.OpenAPI;
import eu.happybe.openapi.form.FormQueue;
import eu.happybe.openapi.form.types.CustomForm;
import eu.happybe.openapi.form.types.ModalForm;
import eu.happybe.openapi.mysql.QueryQueue;
import eu.happybe.openapi.mysql.query.*;
import eu.happybe.openapi.servers.ServerManager;
import eu.happybe.openapi.task.ClosureTask;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class PartyManager {

    private static final Map<String, Party> parties = new HashMap<>();
    private static final Map<String, List<Player>> unloggedPartySessions = new HashMap<>(); // Everyone (owner too) from party whose transferred to current server, they are collected in delayed task
    private static final Map<String, BiConsumer<List<Player>, Party>> offlineSessionHandlers = new HashMap<>();

    public static void createParty(Player player) {
        QueryQueue.submitQuery(new FetchFriendsQuery(player.getName()), query -> {
            List<String> friends = ((FetchFriendsQuery)query).friends.stream().filter(friend -> {
                Player playerFriend = Server.getInstance().getPlayerExact(friend);
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

                QueryQueue.submitQuery(new CreatePartyQuery(player.getName(), ServerManager.getCurrentServer().getServerName()));
                PartyManager.parties.put(player.getName(), new Party(player));

                int i = 0, j = 0;
                for(String friend : friends) {
                    Object value = data[i];
                    if(value instanceof Boolean && ((Boolean) value).booleanValue()) {
                        Player playerFriend = Server.getInstance().getPlayerExact(friend);
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

    public static void sendPartyInvitation(Player owner, Player friend) {
        ModalForm form = new ModalForm("Party invitation", owner.getName() + " invited you to his party!");
        form.setFirstButton("§aAccept invitation");
        form.setSecondButton("§cDecline invitation");

        form.setCallable(response -> {
            if(!owner.isOnline()) {
                friend.sendMessage("§9Parties> §cInvitation expired.");
                return;
            }

            Party party = PartyManager.parties.getOrDefault(owner.getName(), null);
            if(party == null) {
                friend.sendMessage("§9Parties> §cParty doesn't exist anymore.");
                return;
            }

            if(response.getButtonClicked() == 0) {
                party.addMember(friend);
                party.broadcastMessage("§9Party> §a" + friend.getName() + " joined the party!");
            }
        });

        FormQueue.sendForm(friend, form);
    }

    public static Party getPartyByPlayer(Player player) {
        if(PartyManager.parties.containsKey(player.getName())) {
            return PartyManager.parties.get(player.getName());
        }

        for(Party party : PartyManager.parties.values()) {
            if(party.containsPlayer(player)) {
                return party;
            }
        }

        return null;
    }

    public static void destroyParty(Party party) {
        QueryQueue.submitQuery(new DestroyPartyQuery(party.getOwner().getName()));

        for(Player player : party.getMembers().values()) {
            player.sendMessage("§9Party> " + party.getOwner().getName() + " has destroyed his party.");
        }

        PartyManager.removeParty(party);
    }

    public static void removeParty(Party party) {
        PartyManager.parties.remove(party.getOwner().getName());
    }

    public static void handleLoginQuery(Player player, LazyRegisterQuery query) {
        if(query.parties.size() == 0) {
            return;
        }

        String owner = (String) query.parties.getOrDefault("Owner", null);
        String members = (String) query.parties.getOrDefault("Members", null);
        if(owner == null || members == null) {
            player.kick("Unknown party details");
            return;
        }

        List<String> membersList = members.equals("") ? new ArrayList<>() : Arrays.asList(members.split(","));

        if(PartyManager.unloggedPartySessions.containsKey(owner)) {
            PartyManager.unloggedPartySessions.get(owner).add(player);
            return;
        }

        PartyManager.unloggedPartySessions.put(owner, new ArrayList<Player>() {{
            this.add(player);
        }});

        OpenAPI.getInstance().getServer().getScheduler().scheduleDelayedTask(new ClosureTask(i -> {
            Player ownerPlayer = null;

            for(Player member : PartyManager.unloggedPartySessions.get(owner)) {
                if(member.getName().equals(owner)) {
                    ownerPlayer = member;
                }
            }

            BiConsumer<List<Player>, Party> callback = PartyManager.offlineSessionHandlers.getOrDefault(owner, null);

            if(ownerPlayer == null || (!ownerPlayer.isOnline())) {
                QueryQueue.submitQuery(new DestroyPartyQuery(owner));
                for(Player member : PartyManager.unloggedPartySessions.get(owner)) {
                    member.sendMessage("§9Party> §cParty destroyed (It's owner left the game)");
                }

                if(callback != null) {
                    callback.accept(unloggedPartySessions.get(owner).stream().filter(Player::isOnline).collect(Collectors.toList()), null);
                }

                PartyManager.unloggedPartySessions.remove(owner);
                PartyManager.offlineSessionHandlers.remove(owner);
                return;
            }

            Party party = new Party(ownerPlayer);
            Map<String, Player> onlineMembers = new HashMap<>();
            List<String> whoseLeft = new ArrayList<>(membersList);

            for(Player member : PartyManager.unloggedPartySessions.get(owner)) {
                if(member.isOnline()) {
                    onlineMembers.put(member.getName(), member);
                    whoseLeft.remove(member.getName());
                }
            }

            for(String toRemove : whoseLeft) {
                QueryQueue.submitQuery(new RemovePartyMemberQuery(owner, toRemove));
            }
            for(Player inGame : onlineMembers.values()) {
                party.addMember(inGame, false);
            }

            if(whoseLeft.size() > 0) {
                party.broadcastMessage("§9Party> §c" + whoseLeft.size() + " party members left the game.");
            }

            if(callback != null) {
                callback.accept(new ArrayList<>(party.getAll().values()), party);
            }

            QueryQueue.submitQuery(new UpdateRowQuery(new HashMap<String, Object>() {{
                this.put("CurrentServer", ServerManager.getCurrentServer().getServerName());
            }}, "Owner", owner, "Parties"));

            PartyManager.unloggedPartySessions.remove(owner);
            PartyManager.offlineSessionHandlers.remove(owner);

            PartyManager.parties.put(owner, party);
        }), 20);
    }

    public static String isInOfflineQueue(Player player) {
        for(String owner : PartyManager.unloggedPartySessions.keySet()) {
            for(Player pl : PartyManager.unloggedPartySessions.get(owner)) {
                if(pl.getName().equals(player.getName())) {
                    return owner;
                }
            }
        }

        return null;
    }

    public static void addHandlerToOfflineSession(String owner, BiConsumer<List<Player>, Party> callback) {
        PartyManager.offlineSessionHandlers.put(owner, callback);
    }

    public static void handleQuit(Player player) {
        Party party = PartyManager.getPartyByPlayer(player);
        if (party == null) {
            return;
        }

        if (!party.isOnline()) {
            return;
        }

        if (party.getOwner().getName().equals(player.getName())) {
            party.broadcastMessage("§9Party> §cDestroying the party as it's owner (" + player.getName() + ") left the game.");

            PartyManager.destroyParty(party);
            return;
        }

        party.removeMember(player);
    }
}
