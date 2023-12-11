package net.minevn.pluginname

import net.minevn.guiapi.ConfiguredUI
import net.minevn.libs.bukkit.MineVNPlugin
import net.minevn.pluginname.commands.AdminCmd
import net.minevn.pluginname.config.Language
import net.minevn.pluginname.config.MainConfig
import net.minevn.pluginname.gui.ExampleUI

class PluginMainClass : MineVNPlugin() { // TODO: đổi tên package & class
    var prefix = "&8[&6PluginName&8] &7" // TODO: đổi tên plugin mặc định

    // configurations
    lateinit var config: MainConfig private set
    lateinit var language: Language private set

    override fun onEnable() {
        instance = this
        // TODO: plugin start logic

        reload()
        AdminCmd.init()
        UpdateChecker.init()
    }

    fun reload() {
        config = MainConfig()
        prefix = config.prefix
        initDatabase(config.config.getConfigurationSection("database")!!)
        language = Language()

        // GUI
        ExampleUI()
        ConfiguredUI.reloadConfigs(this)
    }

    companion object {
        lateinit var instance: PluginMainClass private set
    }
}
