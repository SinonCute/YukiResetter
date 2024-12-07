package net.minevn.yukiresetter.commands

import net.minevn.libs.bukkit.color
import net.minevn.libs.bukkit.command
import net.minevn.yukiresetter.YukiResetter
import net.minevn.yukiresetter.manager.ResetManager
import net.minevn.yukiresetter.`object`.WorldReset
import net.minevn.yukiresetter.utils.convertHumanReadableTimeToMinutes
import net.minevn.yukiresetter.utils.send
import org.bukkit.Bukkit
import java.util.UUID

class AdminCmd {
    companion object {
        fun init() = command {
            addSubCommand(reload(), "reload")
            addSubCommand(forceStartReset(), "forcestartreset")
            addSubCommand(cancelReset(), "cancelreset")
            addSubCommand(addResetWorld(), "addresetworld")
            addSubCommand(removeResetWorld(), "removeresetworld")
            addSubCommand(setDisplayName(), "setdisplayname")
            addSubCommand(listResetWorld(), "listresetworld")

            action {
                sendSubCommandsUsage(sender, commandTree)
            }

            register(YukiResetter.instance, "yukiresetter")
        }

        private fun reload() = command {
            description("Reload plugin")

            action {
                YukiResetter.instance.reload()
                sender.send("§aĐã reload plugin")
            }
        }

        private fun forceStartReset() = command {
            description("Force start reset")

            action {
                if (args.size != 1) {
                    sender.send("§cSử dụng: /yukiresetter forcestartreset <tên thế giới>")
                    return@action
                }
                val worldName = args[0]
                val schedule = ResetManager.getResetScheduleByWorldName(worldName)
                if (schedule == null) {
                    sender.send("§cKhông tìm thấy thế giới $worldName trong danh sách reset")
                    return@action
                }
                val result = ResetManager.startReset(schedule.id, true)
                if (result) {
                    sender.send("§aĐã bắt đầu reset thế giới $worldName")
                } else {
                    sender.send("§cKhông thể bắt đầu reset thế giới $worldName")
                }
            }

            tabComplete {
                if (args.size == 1) {
                    Bukkit.getWorlds().map { it.name }
                } else {
                    emptyList()
                }
            }
        }

        private fun cancelReset() = command {
            description("Cancel reset")

            action {
                if (args.size != 1) {
                    sender.send("§cSử dụng: /yukiresetter cancelreset <tên thế giới>")
                    return@action
                }
                val worldName = args[0]
                val schedule = ResetManager.getResetScheduleByWorldName(worldName)
                if (schedule == null) {
                    sender.send("§cKhông tìm thấy thế giới $worldName trong danh sách reset")
                    return@action
                }
                if (ResetManager.isResetting(schedule.id)) {
                    ResetManager.cancelReset(schedule.id)
                    sender.send("§aĐã hủy reset thế giới $worldName")
                } else {
                    sender.send("§cThế giới $worldName không đang reset")
                }
            }

            tabComplete {
                if (args.size == 1) {
                    ResetManager.getAllResetSchedules().map { it.worldName }
                } else {
                    emptyList()
                }
            }
        }

        private fun addResetWorld() = command {
            description("Add reset world")

            action {
                val worldName = args[0]
                val resetInterval = convertHumanReadableTimeToMinutes(args[1])
                val borderSize = args[2].toDoubleOrNull() ?: 0.0

                if (worldName.isEmpty() || resetInterval == 0) {
                    sender.send("§cSử dụng: /yukiresetter addresetworld <tên thế giới> <thời gian reset> <kích thước biên giới>")
                    return@action
                }

                if (Bukkit.getWorld(worldName) == null) {
                    sender.send("§cKhông tìm thấy thế giới $worldName")
                    return@action
                }

                if (resetInterval < 60) {
                    sender.send("§cThời gian reset phải lớn hơn 60 phút")
                    return@action
                }

                val scheduleByWorld = ResetManager.getResetScheduleByWorldName(worldName)
                if (scheduleByWorld != null && scheduleByWorld.serverId == YukiResetter.instance.config.serverId) {
                    sender.send("§cThế giới $worldName đã tồn tại trong danh sách reset")
                    return@action
                }

                val schedule = WorldReset(
                    id = UUID.randomUUID().toString(),
                    serverId = YukiResetter.instance.config.serverId,
                    worldDisplayName = worldName,
                    worldName = worldName,
                    worldBorderSize = borderSize,
                    resetInterval = resetInterval.toLong(),
                    lastReset = 0,
                    nextReset = System.currentTimeMillis() + (resetInterval * 1000 * 60)
                )

                ResetManager.setResetSchedule(schedule)

                sender.send("§aĐã thêm thế giới $worldName vào danh sách reset")
            }

            tabComplete {
                when (args.size) {
                    1 -> Bukkit.getWorlds().map { it.name }
                    2 -> listOf("1d", "1w", "1m", "1y")
                    3 -> listOf("0", "1000", "2000", "3000", "4000", "5000")
                    else -> emptyList()
                }
            }
        }

        private fun removeResetWorld() = command {
            description("Remove reset world")

            action {
                if (args.size != 1) {
                    sender.send("§cSử dụng: /yukiresetter removeresetworld <tên thế giới>")
                    return@action
                }
                val worldName = args[0]
                val schedule = ResetManager.getResetScheduleByWorldName(worldName)
                if (schedule == null) {
                    sender.send("§cKhông tìm thấy thế giới $worldName trong danh sách reset")
                    return@action
                }
                ResetManager.deleteResetSchedule(schedule.id)
                sender.send("§aĐã xóa thế giới $worldName khỏi danh sách reset")
            }

            tabComplete {
                if (args.size == 1) {
                    ResetManager.getAllResetSchedules()
                        .filter { it.serverId == YukiResetter.instance.config.serverId }
                        .map { it.worldName }
                } else {
                    emptyList()
                }
            }
        }

        private fun setDisplayName() = command {
            description("Set display name for reset world")

            action {
                if (args.size < 2) {
                    sender.send("§cSử dụng: /yukiresetter setdisplayname <tên thế giới> <tên hiển thị>")
                    return@action
                }
                val worldName = args[0]
                val displayName = args.toList().drop(1).joinToString(" ")
                val schedule = ResetManager.getResetScheduleByWorldName(worldName)
                if (schedule == null) {
                    sender.send("§cKhông tìm thấy thế giới $worldName trong danh sách reset")
                    return@action
                }
                schedule.worldDisplayName = displayName.color()
                ResetManager.setResetSchedule(schedule)
                sender.send("§aĐã đặt tên hiển thị cho thế giới $worldName")
            }

            tabComplete {
                if (args.size == 1) {
                    ResetManager.getAllResetSchedules()
                        .filter { it.serverId == YukiResetter.instance.config.serverId }
                        .map { it.worldName }
                } else {
                    emptyList()
                }
            }
        }

        private fun listResetWorld() = command {
            description("List reset world")

            action {
                val list = ResetManager.getAllResetSchedules()
                if (list.isEmpty()) {
                    sender.send("§cKhông có thế giới nào trong danh sách reset")
                    return@action
                }

                sender.send("§aDanh sách thế giới reset:")
                list.forEach {
                    sender.sendMessage("§a- ${it.worldDisplayName}§r (${it.worldName}) - ${it.resetInterval} phút")
                }
            }
        }
    }
}
