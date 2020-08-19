package eu.bedrockplay.openapi.scoreboard;

import cn.nukkit.Player;
import eu.bedrockplay.openapi.scoreboard.packets.RemoveObjectivePacket;
import eu.bedrockplay.openapi.scoreboard.packets.SetDisplayObjectivePacket;
import eu.bedrockplay.openapi.scoreboard.packets.SetScorePacket;
import eu.bedrockplay.openapi.scoreboard.packets.entry.ScorePacketEntry;

import java.util.*;

public class ScoreboardBuilder {

    private static final Map<String, String> displayedTexts = new HashMap<>();
    private static final Map<String, String[]> scoreBoards = new HashMap<>();
    private static final Map<String, String> titles = new HashMap<>();

    public static void sendScoreBoard(Player player, String text) {
        ScoreboardBuilder.displayedTexts.put(player.getName(), text);

        text = ScoreboardBuilder.formatLines(text);
        text = ScoreboardBuilder.removeDuplicateLines(text);

        String[] splitText = text.split("\n");
        String title = splitText[0];

        splitText = Arrays.copyOfRange(splitText, 1, splitText.length);

        if((!ScoreboardBuilder.titles.containsKey(player.getName())) || (!ScoreboardBuilder.titles.get(player.getName()).equals(title))) {
            if(ScoreboardBuilder.titles.containsKey(player.getName())) {
                ScoreboardBuilder.removeScoreBoard(player);
            }

            ScoreboardBuilder.createScoreBoard(player, title);
        }

        if(!ScoreboardBuilder.scoreBoards.containsKey(player.getName())) {
            ScoreboardBuilder.sendLines(player, splitText);
            ScoreboardBuilder.scoreBoards.put(player.getName(), splitText);
            return;
        }

        ScoreboardBuilder.updateLines(player, ScoreboardBuilder.scoreBoards.get(player.getName()), splitText);
        ScoreboardBuilder.scoreBoards.put(player.getName(), splitText);
    }

    public static void removeScoreBoard(Player player) {
        if(!ScoreboardBuilder.titles.containsKey(player.getName())) {
            return;
        }

        ScoreboardBuilder.titles.remove(player.getName());
        ScoreboardBuilder.scoreBoards.remove(player.getName());
        ScoreboardBuilder.displayedTexts.remove(player.getName());

        RemoveObjectivePacket pk = new RemoveObjectivePacket();
        pk.objectiveName = player.getName().toLowerCase();

        player.dataPacket(pk);
    }

    public static boolean hasObjectiveDisplayed(Player player) {
        return ScoreboardBuilder.titles.containsKey(player.getName());
    }

    public static String getDisplayedText(Player player) {
        return ScoreboardBuilder.displayedTexts.getOrDefault(player.getName(), "");
    }

    public static void createScoreBoard(Player player, String title) {
        SetDisplayObjectivePacket pk = new SetDisplayObjectivePacket();
        pk.objectiveName = player.getName().toLowerCase();
        pk.displayName = title;
        pk.sortOrder = 0; // Ascending
        pk.criteriaName = "dummy";
        pk.displaySlot = "sidebar";

        player.dataPacket(pk);
    }

    public static void sendLines(Player player, String[] splitText) {
        ScoreboardBuilder.sendLines(player, splitText, new int[0]);
    }

    public static void sendLines(Player player, String[] splitText, int[] filter) {
        if(filter != null) {
            for(int i = 0; i < splitText.length; i++) {
                int finalI = i;
                if(Arrays.stream(filter).anyMatch(j -> j == finalI)) {
                    splitText[i] = null;
                }
            }
        }

        ScorePacketEntry[] entries = new ScorePacketEntry[splitText.length];

        for(int i = 0; i < splitText.length; i++) {
            String line = splitText[i];
            if(line != null) {
                ScorePacketEntry entry = new ScorePacketEntry();;
                entry.objectiveName = player.getName().toLowerCase();
                entry.scoreboardId = entry.score = i + 1;
                entry.type = ScorePacketEntry.TYPE_FAKE_PLAYER;
                entry.customName = line;

                entries[i] = entry;
                i++;
            }
        }

        SetScorePacket pk = new SetScorePacket();
        pk.type = SetScorePacket.TYPE_CHANGE;
        pk.entries = entries;

        player.dataPacket(pk);
    }

    private static void  updateLines(Player player, String[] oldSplitText, String[] splitText) {
        if(oldSplitText.length == splitText.length) {
            List<Integer> updateList = new ArrayList<>();

            for(int i = 0; i < splitText.length; i++) {
                if(!oldSplitText[i].equals(splitText[i])) {
                    updateList.add(i);
                }
            }

            ScoreboardBuilder.removeLines(player, updateList.stream().mapToInt(i -> i).toArray());
            ScoreboardBuilder.sendLines(player, splitText, updateList.stream().mapToInt(i -> i).toArray());
            return;
        }

        if(oldSplitText.length > splitText.length) {
            List<Integer> updateList = new ArrayList<>();

            for(int i = 0; i < splitText.length; i++) {
                if(splitText[i] == null) { // in php version, there is isset(), I'm not sure this will work.
                    updateList.add(i);
                    continue;
                }

                if(!splitText[i].equals(oldSplitText[i])) {
                    updateList.add(i);
                }
            }

            int[] lineUpdates = updateList.stream().mapToInt(i -> i).toArray();

            removeLines(player, lineUpdates);
            sendLines(player, splitText, lineUpdates);
            return;
        }

        List<Integer> toRemove = new ArrayList<>();
        List<Integer> toSend = new ArrayList<>();

        int i = 0;
        for(String line : splitText) {
            if(oldSplitText[i] == null) {
                toSend.add(i);
                i++;
                continue;
            }
            if(!oldSplitText[i].equals(line)) {
                toRemove.add(i);
                toSend.add(i);
            }
            i++;
        }

        ScoreboardBuilder.removeLines(player, toRemove.stream().mapToInt(j -> j).toArray());
        ScoreboardBuilder.sendLines(player, splitText, toSend.stream().mapToInt(j -> j).toArray());
    }

    private static void removeLines(Player player, int[] lines) {
        ScorePacketEntry[] entries = new ScorePacketEntry[lines.length];
        int i = 0;
        for(int lineIndex : lines) {
            ScorePacketEntry entry = new ScorePacketEntry();
            entry.objectiveName = player.getName().toLowerCase();
            entry.scoreboardId = entry.score = lineIndex + 1;

            entries[i] = entry;
            i++;
        }

        SetScorePacket pk = new SetScorePacket();
        pk.type = SetScorePacket.TYPE_REMOVE;
        pk.entries = entries;

        player.dataPacket(pk);
    }

    private static String removeDuplicateLines(String text) {
        String[] lines = text.split("\n");

        List<String> used = new ArrayList<>();
        int i = 0;
        for(String line : lines) {
            if(i == 0) {
                i++;
                continue;
            }

            while (used.contains(line)) {
                line += " ";
            }

            lines[i] = line;
            used.add(line);
            i++;
        }

        return String.join("\n", lines);
    }

    private static String formatLines(String text) {
        String[] lines = text.split("\n");
        int i = 0;
        for(String line : lines) {
            if(i == 0) {
                i++;
                continue;
            }

            lines[i] = " " + line + " ";
            i++;
        }

        return String.join("\n", lines);
    }
}
