package eu.happybe.openapi.scoreboard.packets

import cn.nukkit.network.protocol.DataPacket

class SetDisplayObjectivePacket : DataPacket() {
    var displaySlot: String? = null
    var objectiveName: String? = null
    var displayName: String? = null
    var criteriaName: String? = null
    var sortOrder = 0
    override fun pid(): Byte {
        return 0x6b.toByte()
    }

    override fun decode() {}
    override fun encode() {
        reset() // WTF
        putString(displaySlot)
        putString(objectiveName)
        putString(displayName)
        putString(criteriaName)
        putVarInt(sortOrder)
    }
}