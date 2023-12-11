package net.minevn.pluginname.config

import net.minevn.libs.bukkit.color
import net.minevn.pluginname.PluginMainClass

class MainConfig : FileConfig("config") {
    val checkUpdate = config.getBoolean("check-update", true)
    val prefix = config.getString("prefix", PluginMainClass.instance.prefix)!!.color()

    val dbEngine = config.getString("database.engine", "h2")!!
    //TODO: Đổi tên prefix table
    val tablePrefix = config.getString("database.prefix", "myplugin_")!!
}
