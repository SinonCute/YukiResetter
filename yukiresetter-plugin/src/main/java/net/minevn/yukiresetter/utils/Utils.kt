package net.minevn.yukiresetter.utils

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.kyori.adventure.title.Title
import net.minevn.libs.bukkit.color
import net.minevn.yukiresetter.YukiResetter
import net.minevn.yukiresetter.manager.ResetManager
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import java.time.Duration
import java.util.logging.Level
import java.util.regex.Pattern

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
    val regex = Pattern.compile("(\\d+)([^0-9 ])")
    val result = regex.matcher(time)
    if (result.find()) {
        val timeS = result.group(1)
        val unit = result.group(2)
        return when (unit) {
            "s" -> timeS.toInt()
            "m" -> timeS.toInt()
            "h" -> timeS.toInt() * 60
            "d" -> timeS.toInt() * 60 * 24
            "w" -> timeS.toInt() * 60 * 24 * 7
            "y" -> timeS.toInt() * 60 * 24 * 365
            else -> 0
        }
    } else {
        return 0
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
    val worldReset = ResetManager.getResetScheduleByWorldName(worldName) ?: return
    if (worldReset.worldBorderSize > 0) {
        val world = Bukkit.getWorld(worldReset.worldName)
        world?.worldBorder?.size = worldReset.worldBorderSize
    }
}
