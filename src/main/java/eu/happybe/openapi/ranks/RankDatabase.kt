package eu.happybe.openapi.ranks

import cn.nukkit.Player
import eu.happybe.openapi.OpenAPI
import eu.happybe.openapi.mysql.QueryQueue
import eu.happybe.openapi.mysql.query.UpdateRowQuery
import java.util.*

object RankDatabase {
    private val ranks: MutableMap<String, Rank> = HashMap()
    fun init() {
        val ranks = Arrays.asList( // Staff
                Rank("Owner", "§6§l", arrayOf("happybe.operator", "nukkit.command.gamemode", "nukkit.command.teleport", "nukkit.command.kick")),
                Rank("Developer", "§6§l", arrayOf("happybe.operator")),
                Rank("Admin", "§6§l", arrayOf("happybe.operator", "nukkit.command.teleport", "nukkit.command.kick")),
                Rank("Mod", "§e§l", arrayOf("happybe.moderator", "nukkit.command.teleport", "nukkit.command.kick")),
                Rank("Helper", "§e§l", arrayOf("happybe.helper", "nukkit.command.kick")),
                Rank("Builder", "§e§l", arrayOf("happybe.builder")),  // Buyable ranks
                Rank("Bedrock", "§9§l", arrayOf("happybe.bedrock")),
                Rank("MVP", "§3§l", arrayOf("happybe.mvp")),
                Rank("VIP", "§3§l", arrayOf("happybe.vip")),  // Gettable ranks
                Rank("YouTube", "§c§l", arrayOf("happybe.bedrock")),
                Rank("Voter", "§b§l", arrayOf("happybe.voter")),  // Guest
                Rank("Guest", "§b§l", arrayOf(), false)
        )
        for (rank in ranks) {
            RankDatabase.ranks[rank.rankName.toLowerCase()] = rank
        }
    }

    fun savePlayerRank(player: Player, rank: String) {
        savePlayerRank(player, rank, false)
    }

    fun savePlayerRank(player: Player, rank: String, saveToDatabase: Boolean) {
        val targetRank = getRankByName(rank)
        if (targetRank == null) {
            player.kick("Invalid rank ($rank)")
            OpenAPI.getInstance().logger.error("Invalid rank received from database (" + rank + ") for player " + player.name + "!")
            return
        }
        player.namedTag.putString("Rank", targetRank.rankName)
        player.recalculatePermissions()
        for (permission in targetRank.permissions) {
            player.addAttachment(OpenAPI.getInstance(), permission, true)
        }
        if (saveToDatabase) {
            QueryQueue.submitQuery(UpdateRowQuery(object : HashMap<String?, Any?>() {
                init {
                    this["Rank"] = targetRank.rankName
                }
            }, "Name", player.name))
        }
    }

    fun getPlayerRank(player: Player): Rank? {
        return getRankByName(player.namedTag.getString("Rank"))
    }

    fun getRankByName(name: String): Rank? {
        return ranks[name.toLowerCase()]
    }
}