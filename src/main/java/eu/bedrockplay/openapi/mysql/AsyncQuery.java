package eu.bedrockplay.openapi.mysql;

import cn.nukkit.Server;
import cn.nukkit.scheduler.AsyncTask;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class AsyncQuery extends AsyncTask {

    public String host;
    public String user;
    public String password;

    @SneakyThrows
    @Override
    public final void onRun() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();

            Connection connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":3306/" + DatabaseData.DATABASE + "?useSSL=false", this.user, this.password);
            this.query(connection.createStatement());
        }
        catch (Exception e) {
            System.out.println(this.getClass().getName());
            throw e;
        }
    }

    @Override
    public void onCompletion(Server server) {
        QueryQueue.activateCallback(this);
    }

    public abstract void query(Statement statement) throws SQLException;
}
