package net.minevn.yukiresetter.listener

import net.minevn.yukiresetter.manager.ResetManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerTeleportEvent

class PlayerListener : Listener {

    @EventHandler
    fun onPlayerTeleport(event: PlayerTeleportEvent) {
        val player = event.player
        if (event.from.world != event.to.world) {
            val worldName = event.to.world.name
            ResetManager.isResetting(worldName).let {
                event.isCancelled = true
                player.sendMessage("§cKhông thể di chuyển đến thế giới đang được reset!")
            }
        }
    }
}