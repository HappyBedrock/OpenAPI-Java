package eu.happybe.openapi.mysql

import cn.nukkit.Server
import cn.nukkit.scheduler.AsyncTask
import lombok.SneakyThrows
import java.sql.DriverManager
import java.sql.SQLException
import java.sql.Statement

abstract class AsyncQuery : AsyncTask() {
    var host: String? = null
    var user: String? = null
    var password: String? = null
    @SneakyThrows
    override fun onRun() {
        Class.forName("com.mysql.jdbc.Driver").newInstance()
        val connection = DriverManager.getConnection("jdbc:mysql://" + host + ":3306/" + DatabaseData.DATABASE + "?useSSL=false", user, password)
        query(connection.createStatement())
        connection.close()
    }

    override fun onCompletion(server: Server) {
        QueryQueue.activateCallback(this)
    }

    @Throws(SQLException::class)
    abstract fun query(statement: Statement)
}