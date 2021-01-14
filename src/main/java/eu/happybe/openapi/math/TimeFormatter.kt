package eu.happybe.openapi.math

import java.text.SimpleDateFormat
import java.util.*

object TimeFormatter {
    fun getTimeFromString(string: String): Int {
        val values = HashMap<String, Int>()
        values["s"] = 1
        values["m"] = 60
        values["h"] = 60 * 60
        values["d"] = 60 * 60 * 24
        values["w"] = 60 * 60 * 24 * 7
        values["y"] = 60 * 60 * 24 * 365
        var time = 0
        var temp = StringBuilder()
        for (i in 0 until string.length) {
            if (Character.isDigit(string[i])) {
                temp.append(string[i])
                if (i + 1 == string.length) {
                    try {
                        string.toInt()
                    } catch (exception: NumberFormatException) {
                        return 0
                    }
                }
            } else {
                if (values.containsKey(Character.toString(string[i]))) {
                    time += temp.toString().toInt() * values[Character.toString(string[i])]!!
                }
                temp = StringBuilder()
            }
        }
        return time
    }

    fun canFormatTime(time: String): Boolean {
        var isInt = false // first letter must be numeric
        for (i in 0 until time.length) {
            isInt = if (Character.isDigit(time[i])) {
                true
            } else {
                if (!isInt) {
                    return false
                }
                false
            }
        }
        return true
    }

    fun getTimeName(time: Int): String { // Y/m/d
        return SimpleDateFormat("y/M/d H:m:s").format(Date(time))
    }
}