package net.minevn.yukiresetter.manager

import net.minevn.yukiresetter.ResetTask
import net.minevn.yukiresetter.YukiResetter
import net.minevn.yukiresetter.database.WorldResetScheduleDAO
import net.minevn.yukiresetter.`object`.ResetSchedule
import net.minevn.yukiresetter.`object`.WorldReset
import net.minevn.yukiresetter.utils.info
import net.minevn.yukiresetter.utils.runAsyncTimer
import net.minevn.yukiresetter.utils.warning
import org.bukkit.Bukkit

object ResetManager {
    private lateinit var plugin : YukiResetter
    private lateinit var datbase : WorldResetScheduleDAO

    private lateinit var resetSchedules : List<ResetSchedule>
    private val runningTasks = mutableMapOf<String, ResetTask>()

    fun init(plugin: YukiResetter) {
        this.plugin = plugin
        this.datbase = WorldResetScheduleDAO.getInstance()

        resetSchedules = datbase.getAllSchedules()
        info("Loaded ${resetSchedules.filter { it.serverId == plugin.config.serverId }.size} reset schedules for this server")

        runAsyncTimer(::checkDatabase, 0, 20 * 60 * 10) // 10 minutes
        runAsyncTimer(::checkResetTime, 20, 20) // 1 second
        updateDatabase(plugin.config.worldResets)
    }

    private fun updateDatabase(list: List<WorldReset>) {
        list.forEach { worldReset ->
            if (Bukkit.getWorld(worldReset.worldName) == null) {
                warning("world ${worldReset.worldName} not found in server, skipping")
                return
            }
            val schedule = resetSchedules.find { it.worldName == worldReset.worldName
                && it.serverId == plugin.config.serverId
            }
            if (schedule == null) {
                warning("schedule for world ${worldReset.worldName} not found, creating new schedule")
                val newSchedule = ResetSchedule(
                    worldReset.id,
                    plugin.config.serverId,
                    worldReset.worldDisplayName,
                    worldReset.worldName,
                    worldReset.resetInterval,
                    System.currentTimeMillis(),
                    System.currentTimeMillis() + worldReset.resetInterval * 1000 * 60
                )
                datbase.setSchedule(newSchedule)
            }
        }
        datbase.deleteScheduleIfNotExists(plugin.config.serverId, list.map { it.worldName })
        resetSchedules = datbase.getAllSchedules()
    }

    private fun checkResetTime() {
        val needToStart = resetSoon()
        needToStart.forEach {
            startReset(it.id)
        }
    }

    private fun checkDatabase() {
        resetSchedules = datbase.getAllSchedules()
    }

    fun getResetScheduleById(id: String) = resetSchedules.find { it.id == id }

    fun getResetScheduleByWorldName(worldName: String) = resetSchedules.find { it.worldName == worldName }

    fun setResetSchedule(schedule: ResetSchedule) {
        datbase.setSchedule(schedule)
        resetSchedules = datbase.getAllSchedules()
    }

    fun deleteResetSchedule(id: String) {
        datbase.deleteSchedule(id)
        resetSchedules = datbase.getAllSchedules()
    }

    fun getAllResetSchedules() = resetSchedules

    private fun resetSoon() = resetSchedules.filter {
        plugin.config.warningTimes.isNotEmpty() &&
                it.nextReset - System.currentTimeMillis() < plugin.config.warningTimes.first() * 1000
    }

    fun startReset(id: String, force: Boolean = false) : Boolean {
        val schedule = getResetScheduleById(id) ?: return false
        if (runningTasks.containsKey(schedule.id)) return false
        if (plugin.server.getWorld(schedule.worldName) == null) return false

        val task = ResetTask(plugin, schedule, force)
        task.runTaskTimer(plugin, 0, 20)
        runningTasks[schedule.id] = task
        return true
    }

    fun cancelReset(id: String) {
        val task = runningTasks[id] ?: return
        task.cancel()
        runningTasks.remove(id)
        val schedule = getResetScheduleById(id) ?: return
        schedule.nextReset = System.currentTimeMillis() + (schedule.resetInterval * 1000 * 60)
        setResetSchedule(schedule)
    }

    fun finishReset(id: String) {
        runningTasks.remove(id)
    }

    fun isResetting(id: String) = runningTasks.containsKey(id)
}