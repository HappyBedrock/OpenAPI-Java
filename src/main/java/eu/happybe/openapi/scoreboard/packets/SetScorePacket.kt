package eu.happybe.openapi.scoreboard.packets

import cn.nukkit.network.protocol.DataPacket
import eu.happybe.openapi.scoreboard.packets.entry.ScorePacketEntry

class SetScorePacket : DataPacket() {
    var type: Byte = 0
    var entries: Array<ScorePacketEntry>
    override fun pid(): Byte {
        return 0x6c.toByte()
    }

    override fun decode() {}
    override fun encode() {
        reset() // WTF
        putByte(type)
        putUnsignedVarInt(entries.size.toLong())
        for (entry in entries) {
            putVarLong(entry.scoreboardId.toLong())
            putString(entry.objectiveName)
            putLInt(entry.score)
            if (type != TYPE_REMOVE) {
                putByte(entry.type)
                when (entry.type) {
                    ScorePacketEntry.Companion.TYPE_PLAYER, ScorePacketEntry.Companion.TYPE_ENTITY -> putEntityUniqueId(entry.entityUniqueId.toLong())
                    ScorePacketEntry.Companion.TYPE_FAKE_PLAYER -> putString(entry.customName)
                }
            }
        }
    }

    companion object {
        const val TYPE_CHANGE: Byte = 0x00
        const val TYPE_REMOVE: Byte = 0x01
    }
}