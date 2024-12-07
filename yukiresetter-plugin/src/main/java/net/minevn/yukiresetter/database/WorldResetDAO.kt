package net.minevn.yukiresetter.database

import net.minevn.libs.db.DataAccess
import net.minevn.yukiresetter.YukiResetter
import net.minevn.yukiresetter.`object`.WorldReset

abstract class WorldResetDAO : DataAccess() {
    val tableName = "${YukiResetter.instance.config.tablePrefix}world_resets"

    companion object {
        fun getInstance() = YukiResetter.instance.getDAO(WorldResetDAO::class)
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
                val worldBorderSize = getDouble("worldBorderSize")
                val resetInterval = getLong("resetInterval")
                val lastReset = getLong("lastReset")
                val nextReset = getLong("nextReset")
                WorldReset(id, serverId, worldDisplayName, worldName, worldBorderSize, resetInterval, lastReset, nextReset)
            } else null
        }
    }

    fun setSchedule(schedule: WorldReset): Boolean {
        println("Setting schedule for world: ${schedule.worldName}")

        // Check if the table exists
        if (!isTableExists()) {
            println("Table does not exist. Aborting schedule update.")
            return false
        }

        return try {
            val rowsUpdated = setScript().statement {
                setString(1, schedule.id)
                setString(2, schedule.serverId)
                setString(3, schedule.worldDisplayName)
                setString(4, schedule.worldName)
                setDouble(5, schedule.worldBorderSize)
                setLong(6, schedule.resetInterval)
                setLong(7, schedule.lastReset)
                setLong(8, schedule.nextReset)
                executeUpdate()
            }
            println("Schedule set result: ${rowsUpdated > 0}")
            rowsUpdated > 0 // Return true if at least one row was updated/inserted
        } catch (e: Exception) {
            println("Error setting schedule: ${e.message}")
            false // Return false on error
        }
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
            val schedules = mutableListOf<WorldReset>()
            while (next()) {
                val id = getString("id")
                val serverId = getString("serverId")
                val worldDisplayName = getString("worldDisplayName")
                val worldName = getString("worldName")
                val worldBorderSize = getDouble("worldBorderSize")
                val resetInterval = getLong("resetInterval")
                val lastReset = getLong("lastReset")
                val nextReset = getLong("nextReset")
                schedules.add(WorldReset(id, serverId, worldDisplayName, worldName, worldBorderSize, resetInterval, lastReset, nextReset))
            }
            schedules
        }
    }
}