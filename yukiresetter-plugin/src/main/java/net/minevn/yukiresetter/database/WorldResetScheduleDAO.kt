package net.minevn.yukiresetter.database

import net.minevn.libs.db.DataAccess
import net.minevn.yukiresetter.YukiResetter
import net.minevn.yukiresetter.`object`.ResetSchedule

abstract class WorldResetScheduleDAO : DataAccess() {
    val tableName = "${YukiResetter.instance.config.tablePrefix}world_reset_schedule"

    companion object {
        fun getInstance() = YukiResetter.instance.getDAO(WorldResetScheduleDAO::class)
    }

    // region scripts
    abstract fun isTableExistsScript(): String
    abstract fun createTableScript(): String
    abstract fun getScript(): String
    abstract fun getScriptAll(): String
    abstract fun setScript(): String
    abstract fun deleteScript(): String
    abstract fun deleteScriptIfNotExists(worldNamesCount: Int): String
    // endregion

    // region queriers
    fun isTableExists(): Boolean {
        createTableIfNotExists()
        return isTableExistsScript().statement {
            fetch {
                next()
            }
        }
    }

    private fun createTableIfNotExists() {
        createTableScript().statement {
            executeUpdate()
        }
    }

    fun getSchedule(id: String) = if (!isTableExists()) null else getScript().statement {
        setString(1, id)
        fetch {
            if (next()) {
                val serverId = getString("serverId")
                val worldDisplayName = getString("worldDisplayName")
                val worldName = getString("worldName")
                val resetInterval = getLong("resetInterval")
                val lastReset = getLong("lastReset")
                val nextReset = getLong("nextReset")
                ResetSchedule(id, serverId, worldDisplayName, worldName, resetInterval, lastReset, nextReset)
            } else null
        }
    }

    fun setSchedule(schedule: ResetSchedule): Int? {
        val i = if (!isTableExists()) null else setScript().statement {
            setString(1, schedule.id)
            setString(2, schedule.serverId)
            setString(3, schedule.worldDisplayName)
            setString(4, schedule.worldName)
            setLong(5, schedule.resetInterval)
            setLong(6, schedule.lastReset)
            setLong(7, schedule.nextReset)
            executeUpdate()
        }
        return i
    }

    fun deleteSchedule(id: String) = deleteScript().statement {
        setString(1, id)
        executeUpdate()
    }

    fun deleteScheduleIfNotExists(serverId: String, worldNames: List<String>) = deleteScriptIfNotExists(worldNames.size).statement {
        setString(1, serverId)
        worldNames.forEachIndexed { index, worldName ->
            setString(index + 2, worldName)
        }
        executeUpdate()
    }

    fun getAllSchedules() = if (!isTableExists()) emptyList() else getScriptAll().statement {
        fetch {
            val schedules = mutableListOf<ResetSchedule>()
            while (next()) {
                val id = getString("id")
                val serverId = getString("serverId")
                val worldDisplayName = getString("worldDisplayName")
                val worldName = getString("worldName")
                val resetInterval = getLong("resetInterval")
                val lastReset = getLong("lastReset")
                val nextReset = getLong("nextReset")
                schedules.add(ResetSchedule(id, serverId, worldDisplayName, worldName, resetInterval, lastReset, nextReset))
            }
            schedules
        }
    }
}