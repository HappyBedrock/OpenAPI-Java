package eu.happybe.openapi.utils

import cn.nukkit.Player

object DeviceData {
    fun getDeviceName(player: Player): String {
        val deviceOS = player.loginChainData.deviceOS
        when (deviceOS) {
            1 -> return "Android" // Android first :333
            2 -> return "iOS"
            3 -> return "OSX"
            4 -> return "Amazon"
            5 -> return "Gear VR"
            6 -> return "Hololens"
            7 -> return "Windows 10"
            8 -> return "Windows 32" // WTF
            9 -> return "Dedicated"
            10 -> return "TV OS"
            11 -> return "PlayStation"
            12 -> return "Nintendo"
            13 -> return "Xbox"
            14 -> return "Windows Phone"
        }
        return "Unknown"
    }
}