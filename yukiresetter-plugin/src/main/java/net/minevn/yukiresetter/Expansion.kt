package net.minevn.yukiresetter

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import net.minevn.yukiresetter.manager.ResetManager
import org.bukkit.entity.Player

class Expansion : PlaceholderExpansion() {
    override fun getIdentifier(): String {
        return "yukiresetter"
    }

    override fun getAuthor(): String {
        return "Sinon"
    }

    override fun getVersion(): String {
        return "1.0.0"
    }

    override fun persist(): Boolean {
        return true
    }

    override fun onPlaceholderRequest(player: Player, identifier: String): String {
        if (identifier.startsWith("reset_schedule_")) {
            val id = identifier.substringAfter("reset_schedule_").substringBeforeLast("_")
            val schedule = ResetManager.getResetSchedule(id) ?: return "Not found"

            val timeLeft = schedule.nextReset - System.currentTimeMillis()

            val days = timeLeft / 1000 / 60 / 60 / 24
            val hours = (timeLeft / 1000 / 60 / 60) % 24
            val minutes = (timeLeft / 1000 / 60) % 60
            val seconds = (timeLeft / 1000) % 60

            val timeIdentifier = identifier.substringAfterLast("_")

            return when (timeIdentifier) {
                "d" -> days.toString()
                "h" -> hours.toString()
                "m" -> minutes.toString()
                "s" -> seconds.toString()
                else -> "Invalid identifier"
            }
        }
        return ""
    }
}