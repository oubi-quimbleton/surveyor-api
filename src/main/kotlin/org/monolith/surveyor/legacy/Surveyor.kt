package org.monolith.surveyor.legacy

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin
import org.monolith.surveyor.legacy.api.SurveyorAPI
import org.monolith.surveyor.legacy.api.SurveyorAPIImpl
import org.monolith.surveyor.legacy.deed.DeedManager
import org.monolith.surveyor.legacy.deed.DeedSealer
import org.monolith.surveyor.legacy.listeners.PlayerWorldAccessListener
import org.monolith.surveyor.legacy.listeners.ServerListener

class Surveyor : JavaPlugin() {

    // Public API exposed to other plugins
    lateinit var api: SurveyorAPI
        private set

    override fun onEnable() {

        // Core deed system
        val deedManager = DeedManager()
        val deedSealer = DeedSealer()

        // API implementation (handles world load + deed creation)
        val apiImpl = SurveyorAPIImpl(deedManager, deedSealer)
        api = apiImpl

        // Register API as a Bukkit service
        Bukkit.getServicesManager().register(
            SurveyorAPI::class.java,
            apiImpl,
            this,
            ServicePriority.Normal
        )

        // Register server load listener so deeds get created after the server loads
        server.pluginManager.registerEvents(
            ServerListener(apiImpl),
            this
        )

        server.pluginManager.registerEvents(
            PlayerWorldAccessListener(),
            this
        )

        logger.info("Surveyor enabled. API registered as a Bukkit service.")
    }

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {

        if (!command.name.equals("restorechunk", ignoreCase = true))
            return false

        if (sender !is Player) {
            sender.sendMessage("Only players can run this command.")
            return true
        }

        val chunk = sender.location.chunk

        sender.sendMessage("§7Restoring chunk §e(${chunk.x}, ${chunk.z})§7...")

        // This simulates exactly what an external plugin would do:
        api.restoreChunk(chunk)

        sender.sendMessage("§aChunk restored successfully.")
        return true
    }
}