package net.minevn.yukiresetter.commands

import net.minevn.libs.bukkit.command
import net.minevn.yukiresetter.YukiResetter
import net.minevn.yukiresetter.manager.ResetManager
import net.minevn.yukiresetter.utils.convertHumanReadableTimeToMinutes
import net.minevn.yukiresetter.utils.send
import org.bukkit.Bukkit

class AdminCmd {
    companion object {
        fun init() = command {
            addSubCommand(reload(), "reload")
            addSubCommand(forceStartReset(), "forcestartreset")
            addSubCommand(cancelReset(), "cancelreset")
            addSubCommand(addResetWorld(), "addresetworld")
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
                val schedule = ResetManager.getResetSchedule(worldName)
                if (schedule == null) {
                    sender.send("§cKhông tìm thấy thế giới $worldName trong danh sách reset")
                    return@action
                }
                val result = ResetManager.startReset(schedule.worldName, true)
                if (result) {
                    sender.send("§aĐã bắt đầu reset thế giới $worldName")
                } else {
                    sender.send("§cKhông thể bắt đầu reset thế giới $worldName")
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

        private fun cancelReset() = command {
            description("Cancel reset")

            action {
                if (args.size != 1) {
                    sender.send("§cSử dụng: /yukiresetter cancelreset <tên thế giới>")
                    return@action
                }
                val worldName = args[0]
                val schedule = ResetManager.getResetSchedule(worldName)
                if (schedule == null) {
                    sender.send("§cKhông tìm thấy thế giới $worldName trong danh sách reset")
                    return@action
                }
                if (ResetManager.isResetting(worldName)) {
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
                val worldDisplayName = args[2]

                if (Bukkit.getWorld(worldName) == null) {
                    sender.send("§cKhông tìm thấy thế giới $worldName")
                    return@action
                }

                if (resetInterval == 0) {
                    sender.send("§cThời gian không hợp lệ")
                    return@action
                }

                if (ResetManager.getResetSchedule(worldName) != null) {
                    sender.send("§cThế giới $worldName đã tồn tại trong danh sách reset")
                    return@action
                }

                YukiResetter.instance.config.config.set("world_resets.$worldName.reset_interval", resetInterval)
                YukiResetter.instance.config.config.set("world_resets.$worldName.world_display_name", worldDisplayName)
                YukiResetter.instance.config.config.set("world_resets.$worldName.world_name", worldName)
                YukiResetter.instance.config.save()
                sender.send("§aĐã thêm thế giới $worldName vào danh sách reset")
            }

            tabComplete {
                when (args.size) {
                    1 -> Bukkit.getWorlds().map { it.name }
                    2 -> listOf("1d", "1w", "1m", "1y")
                    3 -> listOf("Thế giới 1", "Thế giới 2", "Thế giới 3")
                    else -> emptyList()
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
                    sender.send("§a- ${it.worldDisplayName}§r (${it.worldName}) - ${it.resetInterval} phút")
                }
            }
        }
    }
}
