package eu.happybe.openapi.mysql;

import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class AsyncQuery implements Runnable {

    public String host;
    public String user;
    public String password;

    @SneakyThrows
    public final void run() {
        Class.forName("com.mysql.jdbc.Driver").newInstance();

        Connection connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":3306/" + DatabaseData.DATABASE + "?useSSL=false", this.user, this.password);
        this.query(connection.createStatement());
        connection.close();
    }

    public void onCompletion() {
        QueryQueue.activateCallback(this);
    }

    public abstract void query(Statement statement) throws SQLException;
}
