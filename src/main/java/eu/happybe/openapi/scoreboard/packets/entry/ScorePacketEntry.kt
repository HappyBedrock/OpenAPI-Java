package eu.happybe.openapi.scoreboard.packets.entry

class ScorePacketEntry {
    var scoreboardId = 0
    var objectiveName: String? = null
    var score = 0
    var type: Byte = 0
    var entityUniqueId = 0
    var customName: String? = null

    companion object {
        const val TYPE_PLAYER: Byte = 0x01
        const val TYPE_ENTITY: Byte = 0x02
        const val TYPE_FAKE_PLAYER: Byte = 0x03
    }
}