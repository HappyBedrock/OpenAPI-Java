package eu.happybe.openapi.scoreboard

import cn.nukkit.Player
import eu.happybe.openapi.scoreboard.packets.RemoveObjectivePacket
import eu.happybe.openapi.scoreboard.packets.SetDisplayObjectivePacket
import eu.happybe.openapi.scoreboard.packets.SetScorePacket
import eu.happybe.openapi.scoreboard.packets.entry.ScorePacketEntry
import java.util.*
import java.util.stream.Collectors

object ScoreboardBuilder {
    private val displayedTexts: MutableMap<String, String> = HashMap()
    private val scoreBoards: MutableMap<String, Array<String?>> = HashMap()
    private val titles: MutableMap<String, String> = HashMap()
    fun sendScoreBoard(player: Player, text: String) {
        var text = text
        displayedTexts[player.name] = text
        text = formatLines(text)
        text = removeDuplicateLines(text)
        var splitText: Array<String?> = text.split("\n").toTypedArray()
        val title = splitText[0]
        splitText = Arrays.copyOfRange(splitText, 1, splitText.size)
        if (!titles.containsKey(player.name) || titles[player.name] != title) {
            if (titles.containsKey(player.name)) {
                removeScoreBoard(player)
            }
            createScoreBoard(player, title)
        }
        if (!scoreBoards.containsKey(player.name)) {
            sendLines(player, splitText)
            scoreBoards[player.name] = splitText
            return
        }
        updateLines(player, scoreBoards[player.name]!!, splitText)
        scoreBoards[player.name] = splitText
    }

    fun removeScoreBoard(player: Player) {
        if (!titles.containsKey(player.name)) {
            return
        }
        titles.remove(player.name)
        scoreBoards.remove(player.name)
        displayedTexts.remove(player.name)
        val pk = RemoveObjectivePacket()
        pk.objectiveName = player.name.toLowerCase()
        player.dataPacket(pk)
    }

    fun hasObjectiveDisplayed(player: Player): Boolean {
        return titles.containsKey(player.name)
    }

    fun getDisplayedText(player: Player): String {
        return displayedTexts.getOrDefault(player.name, "")
    }

    fun createScoreBoard(player: Player, title: String?) {
        val pk = SetDisplayObjectivePacket()
        pk.objectiveName = player.name.toLowerCase()
        pk.displayName = title
        pk.sortOrder = 0 // Ascending
        pk.criteriaName = "dummy"
        pk.displaySlot = "sidebar"
        player.dataPacket(pk)
    }

    fun sendLines(player: Player, splitText: Array<String?>) {
        sendLines(player, splitText, null)
    }

    fun sendLines(player: Player, splitText: Array<String?>, filter: IntArray?) {
        var entryCount = splitText.size
        if (filter != null) {
            val filterList = Arrays.stream(filter).boxed().collect(Collectors.toList()) // int[] to List<Integer> convert
            entryCount = 0
            for (i in splitText.indices) {
                if (!filterList.contains(i)) {
                    splitText[i] = null
                    continue
                }
                entryCount++
            }
        }
        val entries = arrayOfNulls<ScorePacketEntry>(entryCount)
        var j = 0
        for (i in splitText.indices) {
            val line = splitText[i]
            if (line != null) {
                val entry = ScorePacketEntry()
                entry.objectiveName = player.name.toLowerCase()
                entry.scoreboardId = i + 1
                entry.score = i + 1
                entry.type = ScorePacketEntry.Companion.TYPE_FAKE_PLAYER
                entry.customName = line
                entries[j++] = entry
            }
        }
        val pk = SetScorePacket()
        pk.type = SetScorePacket.Companion.TYPE_CHANGE
        pk.entries = entries
        player.dataPacket(pk)
    }

    private fun updateLines(player: Player, oldSplitText: Array<String?>, splitText: Array<String?>) {
        if (oldSplitText.size == splitText.size) {
            val updateList: MutableList<Int> = ArrayList()
            for (i in splitText.indices) {
                if (oldSplitText[i] == null || splitText[i] == null || oldSplitText[i] != splitText[i]) {
                    updateList.add(i)
                }
            }
            val updates = updateList.stream().mapToInt { i: Int? -> i!! }.toArray()
            removeLines(player, updates)
            sendLines(player, splitText, updates)
            return
        }
        if (oldSplitText.size > splitText.size) {
            val updateList: MutableList<Int> = ArrayList()
            for (i in oldSplitText.indices) {
                if (i >= splitText.size || splitText[i] == null) {
                    updateList.add(i)
                    continue
                }
                if (splitText[i] != oldSplitText[i]) {
                    updateList.add(i)
                }
            }
            val lineUpdates = updateList.stream().mapToInt { i: Int? -> i!! }.toArray()
            removeLines(player, lineUpdates)
            sendLines(player, splitText, lineUpdates)
            return
        }
        val toRemove: MutableList<Int> = ArrayList()
        val toSend: MutableList<Int> = ArrayList()
        for (i in splitText.indices) {
            val line = splitText[i]
            if (i >= oldSplitText.size || oldSplitText[i] == null) {
                toSend.add(i)
                continue
            }
            if (oldSplitText[i] != line) {
                toRemove.add(i)
                toSend.add(i)
            }
        }
        removeLines(player, toRemove.stream().mapToInt { j: Int? -> j!! }.toArray())
        sendLines(player, splitText, toSend.stream().mapToInt { j: Int? -> j!! }.toArray())
    }

    private fun removeLines(player: Player, lines: IntArray) {
        val entries = arrayOfNulls<ScorePacketEntry>(lines.size)
        for (i in lines.indices) {
            val lineNumber = lines[i]
            val entry = ScorePacketEntry()
            entry.objectiveName = player.name.toLowerCase()
            entry.scoreboardId = lineNumber + 1
            entry.score = lineNumber + 1
            entries[i] = entry
        }
        val pk = SetScorePacket()
        pk.type = SetScorePacket.Companion.TYPE_REMOVE
        pk.entries = entries
        player.dataPacket(pk)
    }

    private fun removeDuplicateLines(text: String): String {
        val lines = text.split("\n").toTypedArray()
        val used: MutableList<String> = ArrayList()
        var i = 0
        for (line in lines) {
            if (i == 0) {
                i++
                continue
            }
            while (used.contains(line)) {
                line += " "
            }
            lines[i] = line
            used.add(line)
            i++
        }
        return java.lang.String.join("\n", *lines)
    }

    private fun formatLines(text: String): String {
        val lines = text.split("\n").toTypedArray()
        var i = 0
        for (line in lines) {
            if (i == 0) {
                i++
                continue
            }
            lines[i] = " $line "
            i++
        }
        return java.lang.String.join("\n", *lines)
    }
}