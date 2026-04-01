package org.monolith.surveyor.legacy.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerTeleportEvent

internal class PlayerWorldAccessListener : Listener {

    @EventHandler
    fun onTeleport(event: PlayerTeleportEvent) {
        val to = event.to ?: return
        val world = to.world ?: return

        if (world.name.endsWith("_deed")) {
            event.isCancelled = true
            event.player.sendMessage("§cYou cannot enter this world.")
        }
    }
}