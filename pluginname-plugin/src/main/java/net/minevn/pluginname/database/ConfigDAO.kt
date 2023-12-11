package net.minevn.pluginname.database

import net.minevn.libs.db.DataAccess
import net.minevn.pluginname.PluginMainClass

abstract class ConfigDAO : DataAccess() {
    // TODO: đổi tên table
    val tableName = "${PluginMainClass.instance.config.tablePrefix}config"

    companion object {
        fun getInstance() = PluginMainClass.instance.getDAO(ConfigDAO::class)
    }

    // region scripts
    abstract fun isTableExistsScript(): String
    abstract fun getScript(): String
    abstract fun setScript(): String
    abstract fun deleteScript(): String
    // endregion

    // region queriers
    fun isTableExists() = isTableExistsScript().statement {
        executeQuery().use { it.next() }
    }

    fun get(key: String) = if (!isTableExists()) null else getScript().statement {
        setString(1, key)
        fetch {
            if (next()) getString("value")
            else null
        }
    }

    fun set(key: String, value: String) = if (!isTableExists()) null else setScript().statement {
        setString(1, key)
        setString(2, value)
        executeUpdate()
    }

    fun delete(key: String) = deleteScript().statement {
        setString(1, key)
        executeUpdate()
    }
    // endregion
}
