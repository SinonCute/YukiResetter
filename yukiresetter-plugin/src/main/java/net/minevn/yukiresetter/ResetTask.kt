package net.minevn.yukiresetter

import net.minevn.yukiresetter.manager.ResetManager
import net.minevn.yukiresetter.`object`.WorldReset
import net.minevn.yukiresetter.utils.resetWorld
import net.minevn.yukiresetter.utils.send
import net.minevn.yukiresetter.utils.sendServerMessages
import net.minevn.yukiresetter.utils.sendTitle
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import kotlin.math.ceil

class ResetTask(
    private val plugin: YukiResetter,
    private val schedule: WorldReset,
    force: Boolean = false
) : BukkitRunnable() {

    private val config = plugin.config
    private val lang = plugin.language
    private val warningTimes = config.warningTimes
    private val nextReset = if (force) System.currentTimeMillis() + (warningTimes.first() * 1000) else schedule.nextReset

    override fun run() {
        val timeLeft = ceil((nextReset - System.currentTimeMillis()) / 1000.0).toInt()
        val players = plugin.server.getWorld(schedule.worldName)?.players ?: return

        when (timeLeft) {
            in warningTimes -> showWarning(players, timeLeft)
            1 -> prepareForReset(players)
            0 -> performReset()
        }
    }

    private fun showWarning(players: List<Player>, timeLeft: Int) {
        val warningMessage = lang.warningChatMessage
            .replace("%WORLD%", schedule.worldDisplayName)
            .replace("%TIME%", timeLeft.toString())

        players.forEach {
            it.send(warningMessage)
            sendTitle(it,
                lang.warningTitle.replace("%WORLD%", schedule.worldDisplayName).replace("%TIME%", timeLeft.toString()),
                lang.warningSubtitle.replace("%WORLD%", schedule.worldDisplayName).replace("%TIME%", timeLeft.toString()),
                0, 500, 0
            )
        }
    }

    private fun prepareForReset(players: List<Player>) {
        val resetMessage = lang.resetChatMessage.replace("%WORLD%", schedule.worldDisplayName)
        sendServerMessages(listOf(resetMessage))

        players.forEach {
            it.sendMessage(config.prefix + lang.teleportingMessage)
            val teleportCommand = config.teleportCommand.replace("{player}", it.name)
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), teleportCommand)
        }
    }

    private fun performReset() {
        resetWorld(schedule.worldName)

        val resetCompleteMessage = lang.resetCompleteChatMessage.replace("%WORLD%", schedule.worldDisplayName)
        sendServerMessages(listOf(resetCompleteMessage))

        // Update reset schedule times
        schedule.lastReset = System.currentTimeMillis()
        schedule.nextReset = System.currentTimeMillis() + (schedule.resetInterval * 1000 * 60)
        ResetManager.setResetSchedule(schedule)
        ResetManager.finishReset(schedule.id)

        cancel()
    }
}
