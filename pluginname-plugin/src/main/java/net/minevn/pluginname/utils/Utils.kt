package net.minevn.pluginname.utils

import net.minevn.libs.bukkit.color
import net.minevn.pluginname.PluginMainClass
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import java.util.logging.Level

private val main = PluginMainClass.instance

fun info(message: String) = main.logger.info(message)
fun warning(message: String) = main.logger.warning(message)
fun Exception.warning(message: String) = main.logger.log(Level.WARNING, message, this)
fun Exception.severe(message: String) = main.logger.log(Level.SEVERE, message, this)

fun sendServerMessages(statusMessages: List<String>) = statusMessages.forEach {
    Bukkit.broadcastMessage("${main.config.prefix} $it".color())
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
