package org.monolith.surveyor.legacy.listeners

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerLoadEvent
import org.monolith.surveyor.legacy.api.SurveyorAPI

internal class ServerListener(
    private val api: SurveyorAPI
) : Listener {

    @EventHandler
    fun onServerLoad(event: ServerLoadEvent) {
        for (world in Bukkit.getWorlds()) {
            api.handleWorldLoad(world)
        }
    }
}
