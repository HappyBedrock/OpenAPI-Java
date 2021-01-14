package eu.happybe.openapi.stats

import cn.nukkit.Player
import eu.happybe.openapi.mysql.QueryQueue
import eu.happybe.openapi.mysql.query.AddPointQuery

enum class Stats(@field:Getter private val columnName: String, @field:Getter private val table: String) {
    POINT_CRYSTAL("Crystals", "Values"), POINT_UHCRUN_WIN("UHCRunWins", "Stats"), POINT_UHCRUN_KILL("UHCRunKills", "Stats");

    companion object {
        fun addPoint(player: Player, stat: Stats) {
            QueryQueue.submitQuery(AddPointQuery(player.name, stat.getColumnName(), stat.getTable()))
        }
    }
}