package eu.happybe.openapi.math;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class TimeFormatter {

    public static int getTimeFromString(String string) {
        HashMap<String, Integer> values = new HashMap<>();

        values.put("s", 1);
        values.put("m", 60);
        values.put("h", 60 * 60);
        values.put("d", 60 * 60 * 24);
        values.put("w", 60 * 60 * 24 * 7);
        values.put("y", 60 * 60 * 24 * 365);

        int time = 0;
        StringBuilder temp = new StringBuilder();

        for(int i = 0; i < string.length(); i++) {
            if(Character.isDigit(string.charAt(i))) {
                temp.append(string.charAt(i));
                if(i + 1 == string.length()) {
                    try {
                        Integer.parseInt(string);
                    }
                    catch (NumberFormatException exception) {
                        return 0;
                    }
                }
            }
            else {
                if(values.containsKey(Character.toString(string.charAt(i)))) {
                    time += (Integer.parseInt(temp.toString())) * values.get(Character.toString(string.charAt(i)));
                }
                temp = new StringBuilder();
            }
        }

        return time;
    }

    public static boolean canFormatTime(String time) {
        boolean isInt = false; // first letter must be numeric
        for(int i = 0; i < time.length(); i++) {
            if(Character.isDigit(time.charAt(i))) {
                isInt = true;
            }
            else {
                if(!isInt) {
                    return false;
                }
                isInt = false;
            }
        }

        return true;
    }

    public static String getTimeName(int time) { // Y/m/d
        return new SimpleDateFormat("y/M/d H:m:s").format(new Date(time));
    }
}