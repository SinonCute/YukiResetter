package net.minevn.pluginname.commands

import net.minevn.libs.bukkit.command
import net.minevn.pluginname.PluginMainClass
import net.minevn.pluginname.utils.send

class AdminCmd {
    companion object {
        fun init() = command {
            addSubCommand(reload(), "reload")

            action {
                sendSubCommandsUsage(sender, commandTree)
            }

            register(PluginMainClass.instance, "pluginname")
        }

        private fun reload() = command {
            description("Reload plugin")

            action {
                PluginMainClass.instance.reload()
                sender.send("§aĐã reload plugin")
            }
        }
    }
}
