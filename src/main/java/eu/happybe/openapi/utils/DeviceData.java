package eu.happybe.openapi.utils;

import cn.nukkit.Player;

public class DeviceData {

    public static String getDeviceName(Player player) {
        int deviceOS = player.getLoginChainData().getDeviceOS();

        switch (deviceOS) {
            case 1:
                return "Android"; // Android first :333
            case 2:
                return "iOS";
            case 3:
                return "OSX";
            case 4:
                return "Amazon";
            case 5:
                return "Gear VR";
            case 6:
                return "Hololens";
            case 7:
                return "Windows 10";
            case 8:
                return "Windows 32"; // WTF
            case 9:
                return "Dedicated";
            case 10:
                return "TV OS";
            case 11:
                return "PlayStation";
            case 12:
                return "Nintendo";
            case 13:
                return "Xbox";
            case 14: // should be before iOS
                return "Windows Phone";
        }

        return "Unknown";
    }
}
