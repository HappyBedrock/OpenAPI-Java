package eu.happybe.openapi.mysql;

import lombok.Getter;

public class DatabaseData {

    public static final String DATABASE = "HappyBE_Test";

    @Getter
    private static String host;
    @Getter
    private static String user;
    @Getter
    private static String password;

    public static void update(String host, String user, String password) {
        DatabaseData.host = host;
        DatabaseData.user = user;
        DatabaseData.password = password;
    }
}
