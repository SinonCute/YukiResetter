package net.minevn.yukiresetter.config

import net.minevn.libs.bukkit.FileConfig
import net.minevn.yukiresetter.YukiResetter

open class FileConfig(fileName: String) : FileConfig(YukiResetter.instance, fileName) {
    val main = YukiResetter.instance

    override fun get(key: String) = super.get(key).replace("%PREFIX%", main.prefix)

    override fun getList(key: String) = super.getList(key).map { it.replace("%PREFIX%", main.prefix) }
}
