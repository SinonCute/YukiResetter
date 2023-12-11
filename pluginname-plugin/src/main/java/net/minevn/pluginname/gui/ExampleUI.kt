package net.minevn.pluginname.gui

import net.minevn.guiapi.ConfiguredUI
import net.minevn.guiapi.GuiIcon.Companion.getGuiIcon
import net.minevn.libs.bukkit.MineVNLib.Companion.getGuiFillSlots
import net.minevn.pluginname.PluginMainClass
import net.minevn.pluginname.utils.runNotSync
import org.bukkit.entity.Player

class ExampleUI(viewer: Player?) :
    ConfiguredUI(viewer, "menu/example.yml", PluginMainClass.instance) {

    constructor() : this(null)

    init {
        if (viewer?.isOnline == true) buildAsync()
    }

    private fun build() {
        val viewer = viewer!!

        val config = getConfig()
        lock()

        // background
        setItem(
            config.getGuiFillSlots("background.fill"),
            config.getGuiIcon("background").toGuiItemStack()
        )

        // close button
        setItem(
            config.getInt("close.slot"),
            config.getGuiIcon("close").toGuiItemStack {
               viewer.closeInventory()
            }
        )

        // TODO thêm giao diện cũng như luồng xử lí giao diện ở đây

        unlock()

        // open to viewer
        open()
    }

    private fun buildAsync() = runNotSync { build() }
}
