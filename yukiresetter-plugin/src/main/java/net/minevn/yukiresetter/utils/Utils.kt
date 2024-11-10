package net.minevn.yukiresetter.utils

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.kyori.adventure.title.Title
import net.minevn.libs.bukkit.color
import net.minevn.yukiresetter.YukiResetter
import net.minevn.yukiresetter.`object`.WorldReset
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import java.time.Duration
import java.util.logging.Level

private val main = YukiResetter.instance

fun info(message: String) = main.logger.info(message)
fun warning(message: String) = main.logger.warning(message)
fun Exception.warning(message: String) = main.logger.log(Level.WARNING, message, this)
fun Exception.severe(message: String) = main.logger.log(Level.SEVERE, message, this)

fun sendServerMessages(statusMessages: List<String>, prefix: Boolean = true) = statusMessages.forEach {
    Bukkit.broadcastMessage("${if (prefix) main.config.prefix else ""} $it".color())
}

fun CommandSender.send(message: String) = sendMessage("${main.config.prefix} $message".color())

fun runAsync(r: Runnable) {
    Bukkit.getScheduler().runTaskAsynchronously(main, r)
}

fun runNotSync(r: Runnable) {
    if (Bukkit.isPrimaryThread()) {
        Bukkit.getScheduler().runTaskAsynchronously(main, r)
    } else {
        r.run()
    }
}

fun runSync(r: Runnable) {
    if (Bukkit.isPrimaryThread()) {
        r.run()
    } else {
        Bukkit.getScheduler().runTask(main, r)
    }
}

fun runLater(r: Runnable, delay: Long) {
    Bukkit.getScheduler().runTaskLater(main, r, delay)
}

fun runAsyncLater(r: Runnable, delay: Long) {
    Bukkit.getScheduler().runTaskLaterAsynchronously(main, r, delay)
}

fun runAsyncTimer(r: Runnable, delay: Long, period: Long) {
    Bukkit.getScheduler().runTaskTimerAsynchronously(main, r, delay, period)
}

fun runSyncTimer(r: Runnable, delay: Long, period: Long) {
    Bukkit.getScheduler().runTaskTimer(main, r, delay, period)
}

fun sendTitle(target: Audience, title: String, subtitle: String, time: Int) {
    sendTitle(target, title, subtitle, 0, time, 0)
}

fun sendTitle(target: Audience, title: String, subtitle: String, fadeIn: Int, stay: Int, fadeOut: Int) {
    val times = Title.Times.times(Duration.ofMillis(fadeIn.toLong()), Duration.ofMillis(stay.toLong()), Duration.ofMillis(fadeOut.toLong()))
    val titleComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(title)
    val subtitleComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(subtitle)
    val titleObject = Title.title(titleComponent, subtitleComponent, times)
    target.showTitle(titleObject)
}

fun convertHumanReadableTimeToMinutes(time: String): Int {
    val time = time.split(" ")
    return when (time.size) {
        1 -> {
            time[0].toInt()
        }
        2 -> {
            val minutes = time[0].toInt()
            val unit = time[1]
            when (unit) {
                "s" -> minutes
                "m" -> minutes
                "h" -> minutes * 60
                "d" -> minutes * 60 * 24
                "w" -> minutes * 60 * 24 * 7
                "y" -> minutes * 60 * 24 * 365
                else -> 0
            }
        }
        else -> 0
    }
}

fun resetWorld(worldName: String) {
    val worldManager = YukiResetter.multiverseCore.mvWorldManager
    val worldEnvironment = worldManager.getMVWorld(worldName).environment
    val worldType = worldManager.getMVWorld(worldName).worldType
    if (!worldManager.unloadWorld(worldName)) {
        warning("Failed to unload world $worldName")
    }
    if (!worldManager.deleteWorld(worldName, true, true)) {
        warning("Failed to delete world $worldName")
    }
    if (!worldManager.addWorld(worldName, worldEnvironment, null, worldType, true, null, true)) {
        warning("Failed to add world $worldName")
    }
    val worldReset = main.config.worldResets.find { it.worldName == worldName } ?: return
    if (worldReset.worldBorderSize > 0) {
        val world = Bukkit.getWorld(worldReset.worldName)
        world?.worldBorder?.size = worldReset.worldBorderSize
    }
}
