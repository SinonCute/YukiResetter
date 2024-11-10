package net.minevn.yukiresetter.config

import net.minevn.libs.bukkit.color
import net.minevn.yukiresetter.YukiResetter
import net.minevn.yukiresetter.`object`.WorldReset

class MainConfig : FileConfig("config") {
    val prefix = config.getString("prefix", YukiResetter.instance.prefix)!!.color()

    val dbEngine = config.getString("database.engine", "h2")!!
    val tablePrefix = config.getString("database.prefix", "yukiresetter_")!!

    val serverId = config.getString("server_id")!!

    val warningTimes: MutableList<Int> = config.getIntegerList("notifications.warning_times")
    val worldResets = config.getMapList("world_resets").map {
        val worldName = it["world_name"] as String
        val worldDisplayName = it["world_display_name"] as String
        val worldBorderSize = (it["world_border_size"] as String).toDouble()
        val resetInterval = (it["reset_interval"] as String).toLong()
        WorldReset(worldName, worldDisplayName, worldName, worldBorderSize, resetInterval)
    }
    val teleportCommand = config.getString("teleport_command", "spawn {player}")!!

}
