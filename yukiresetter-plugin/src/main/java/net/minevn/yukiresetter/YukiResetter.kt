package net.minevn.yukiresetter

import com.onarandombox.MultiverseCore.MultiverseCore
import net.minevn.libs.bukkit.MineVNPlugin
import net.minevn.yukiresetter.commands.AdminCmd
import net.minevn.yukiresetter.config.Language
import net.minevn.yukiresetter.config.MainConfig
import net.minevn.yukiresetter.manager.ResetManager

class YukiResetter : MineVNPlugin() {
    var prefix = "&b&lYukiResetter &8» &r"

    lateinit var config: MainConfig private set
    lateinit var language: Language private set

    override fun onEnable() {
        instance = this
        multiverseCore = server.pluginManager.getPlugin("Multiverse-Core") as MultiverseCore

        reload()
        AdminCmd.init()
    }

    fun reload() {
        config = MainConfig()
        prefix = config.prefix
        initDatabase(config.config.getConfigurationSection("database")!!)
        language = Language()

        ResetManager.init(this)
        Expansion().register();
    }

    companion object {
        lateinit var instance: YukiResetter private set
        lateinit var multiverseCore: MultiverseCore private set
    }
}
