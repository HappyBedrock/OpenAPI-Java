package eu.happybe.openapi.mysql

import lombok.Getter

object DatabaseData {
    const val DATABASE = "HappyBE"

    @Getter
    private var host: String? = null

    @Getter
    private var user: String? = null

    @Getter
    private var password: String? = null
    fun update(host: String?, user: String?, password: String?) {
        DatabaseData.host = host
        DatabaseData.user = user
        DatabaseData.password = password
    }
}