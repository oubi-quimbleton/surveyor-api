package org.monolith.surveyor.api

import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block
import org.monolith.surveyor.deed.DeedManager
import org.monolith.surveyor.deed.DeedSealer
import org.monolith.surveyor.restore.RestoreManager

internal class SurveyorAPIImpl(
    private val deedManager: DeedManager,
    private val deedSealer: DeedSealer
) : SurveyorAPI {

    private val worldDeeds = mutableMapOf<String, World>()
    private val restoreManagers = mutableMapOf<World, RestoreManager>()

    override fun handleWorldLoad(world: World) {
        // Skip deed worlds themselves
        if (world.name.endsWith("_deed")) return

        // Create or load the deed world
        val deedWorld = deedManager.loadWorldDeed(world)

        // Seal it (ensures it's static and safe)
        deedSealer.seal(deedWorld)

        // Store mapping
        worldDeeds[world.name] = deedWorld
    }

    private fun getDeedFor(world: World): World? {
        return worldDeeds[world.name]
    }

    private fun getRestoreManagerFor(world: World): RestoreManager {
        val deed = getDeedFor(world)
            ?: throw IllegalStateException("Deed not ready for ${world.name}")

        return restoreManagers.getOrPut(deed) {
            RestoreManager(deed)
        }
    }

    // -------------------------
    // New API methods
    // -------------------------

    override fun restoreAt(block: Block) {
        val rm = getRestoreManagerFor(block.world)
        rm.restoreAt(block)
    }

    override fun restoreChunk(targetChunk: Chunk) {
        val rm = getRestoreManagerFor(targetChunk.world)
        rm.restoreChunk(targetChunk)
    }

    override fun restoreRegion(min: Location, max: Location) {
        val rm = getRestoreManagerFor(min.world!!)
        rm.restoreRegion(min, max)
    }

    override fun addRestoreListener(listener: RestoreListener) {
        // Add listener to all existing managers
        restoreManagers.values.forEach { it.addRestoreListener(listener) }
    }

    override fun removeRestoreListener(listener: RestoreListener) {
        restoreManagers.values.forEach { it.removeRestoreListener(listener) }
    }
}
