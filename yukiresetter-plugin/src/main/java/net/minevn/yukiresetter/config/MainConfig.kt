package net.minevn.yukiresetter.config

import net.minevn.libs.bukkit.color
import net.minevn.yukiresetter.YukiResetter

class MainConfig : FileConfig("config") {
    val prefix = config.getString("prefix", YukiResetter.instance.prefix)!!.color()

    val dbEngine = config.getString("database.engine", "h2")!!
    val tablePrefix = config.getString("database.prefix", "yukiresetter_")!!

    val serverId = config.getString("server_id")!!

    val warningTimes: MutableList<Int> = config.getIntegerList("notifications.warning_times")
    val teleportCommand = config.getString("teleport_command", "spawn {player}")!!
}
