package net.minevn.pluginname

import net.minevn.libs.bukkit.parseJson
import net.minevn.libs.get
import net.minevn.pluginname.utils.runNotSync
import net.minevn.pluginname.utils.warning
import org.bukkit.command.CommandSender

class UpdateChecker {

    companion object {
        private const val URL = "" //TODO Thay URL bằng URL API của GitHub hoặc trang web cung cấp thông tin cập nhật
        private val plugin = PluginMainClass.instance
        private val language = plugin.language
        private var releaseVersion : String = ""
        private val currentVersion = plugin.description.version.trim()
        private var latestVersion = currentVersion
        private var latest = false

        fun init() {
            runNotSync {
                latest = checkUpdate()
                sendUpdateMessage(plugin.server.consoleSender, true)
            }
        }

        fun sendUpdateMessage(receiver: CommandSender, notifyLatestVersion: Boolean = false) {
            //TODO: Thay tên pluginName bằng tên plugin của bạn
            if (!plugin.config.checkUpdate || !receiver.hasPermission("pluginName.update")) return
            if (!latest) {
                language.updateAvailable
                    .replace("%NEW_VERSION%", latestVersion)
                    .replace("%CURRENT_VERSION%", currentVersion)
                    .let { receiver.sendMessage(it) }
                receiver.sendMessage(language.updateAvailableLink.replace("%URL%", releaseVersion))
                return
            }
            if (notifyLatestVersion) {
                receiver.sendMessage(language.updateLatest)
            }
        }

        private fun checkUpdate(): Boolean {
            //Đây là phần kiểm tra cập nhật thông qua GitHub API, bạn có thể thay đổi cách kiểm tra cập nhật
            try {
                get(URL).parseJson().asJsonObject.let {
                    latestVersion = it["tag_name"].asString
                    releaseVersion = it["html_url"].asString
                    return latestVersion == currentVersion
                }
            } catch (e: Exception) {
                e.warning("Không thể kiểm tra cập nhật")
                return true
            }
        }
    }
}
