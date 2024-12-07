package net.minevn.yukiresetter.manager

import net.minevn.yukiresetter.ResetTask
import net.minevn.yukiresetter.YukiResetter
import net.minevn.yukiresetter.database.WorldResetDAO
import net.minevn.yukiresetter.`object`.WorldReset
import net.minevn.yukiresetter.utils.info
import net.minevn.yukiresetter.utils.runAsyncTimer
import net.minevn.yukiresetter.utils.warning
import org.bukkit.Bukkit

object ResetManager {
    private lateinit var plugin : YukiResetter
    private lateinit var database : WorldResetDAO

    private lateinit var schedules : List<WorldReset>
    private val runningTasks = mutableMapOf<String, ResetTask>()

    fun init(plugin: YukiResetter) {
        this.plugin = plugin
        this.database = WorldResetDAO.getInstance()

        schedules = database.getAllSchedules()
        info("Loaded ${schedules.filter { it.serverId == plugin.config.serverId }.size} reset schedules for this server")

        runAsyncTimer(::checkResetTime, 20, 20) // 1 second
        checkData(schedules)
    }

    private fun checkData(list: List<WorldReset>) {
        list.forEach { worldReset ->
            if (Bukkit.getWorld(worldReset.worldName) == null) {
                warning("Schedule ${worldReset.worldName} is not valid, because the world does not exist")
                return
            }
        }
    }

    private fun checkResetTime() {
        val needToStart = resetSoon()
        needToStart.forEach {
            startReset(it.id)
        }
    }

    fun getResetScheduleById(id: String) = schedules.find { it.id == id }

    fun getResetScheduleByWorldName(worldName: String) = schedules.find { it.worldName == worldName }

    fun setResetSchedule(schedule: WorldReset) {
        println(database.setSchedule(schedule))
        schedules = database.getAllSchedules()
    }

    fun deleteResetSchedule(id: String) {
        database.deleteSchedule(id)
        schedules = database.getAllSchedules()
    }

    fun getAllResetSchedules() = schedules

    private fun resetSoon() = schedules.filter {
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