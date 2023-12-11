package net.minevn.pluginname.config

import net.minevn.libs.bukkit.FileConfig
import net.minevn.pluginname.PluginMainClass

open class FileConfig(fileName: String) : FileConfig(PluginMainClass.instance, fileName) {
    val main = PluginMainClass.instance

    override fun get(key: String) = super.get(key).replace("%PREFIX%", main.prefix)

    override fun getList(key: String) = super.getList(key).map { it.replace("%PREFIX%", main.prefix) }
}
