package eu.happybe.openapi.scoreboard.packets

import cn.nukkit.network.protocol.DataPacket

class RemoveObjectivePacket : DataPacket() {
    var objectiveName: String? = null
    override fun pid(): Byte {
        return 0x6a.toByte()
    }

    override fun decode() {}
    override fun encode() {
        reset()
        putString(objectiveName)
    }
}