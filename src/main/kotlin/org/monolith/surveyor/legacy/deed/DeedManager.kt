package org.monolith.surveyor.legacy.deed

import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.WorldCreator

internal class DeedManager {

    fun loadWorldDeed(sourceWorld: World): World {
        val deedName = "${sourceWorld.name}_deed"

        // If already loaded, return it
        Bukkit.getWorld(deedName)?.let { return it }

        // Create a new deed world with identical worldgen settings
        val creator = WorldCreator(deedName).apply {
            seed(sourceWorld.seed)
            environment(sourceWorld.environment)
        }

        return creator.createWorld()
            ?: throw IllegalStateException("Failed to create deed for world: ${sourceWorld.name}")
    }
}
