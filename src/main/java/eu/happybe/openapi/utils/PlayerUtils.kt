package eu.happybe.openapi.utils

import cn.nukkit.Player
import eu.happybe.openapi.ranks.RankDatabase

object PlayerUtils {
    fun updateNameTag(player: Player) {
        updateNameTag(player, if (player.namedTag.exist("NameTagColor")) player.namedTag.getString("NameTagColor") else "§e")
    }

    fun updateNameTag(player: Player, color: String) {
        var rank = RankDatabase.getPlayerRank(player)
        if (rank == null) {
            rank = RankDatabase.getRankByName("Guest")
        }
        player.namedTag.putString("NameTagColor", color)
        player.nameTag = """
               ${rank.formatForNameTag}$color${player.name}
               §b${DeviceData.getDeviceName(player)}
               """.trimIndent()
    }
}