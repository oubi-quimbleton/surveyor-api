package org.monolith.surveyor.legacy.api

import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block

interface SurveyorAPI {

    /**
     * Creates a deed for the newly loaded world if one does not already exist.
     */
    fun handleWorldLoad(world: World)

    /**
     * Restores the chunk containing this block from the source (deed) world.
     */
    fun restoreAt(block: Block)

    /**
     * Restores the given chunk from the source (deed) world.
     */
    fun restoreChunk(targetChunk: Chunk)

    /**
     * Restores all chunks intersecting the given region from the source (deed) world.
     */
    fun restoreRegion(min: Location, max: Location)

    /**
     * Registers a listener that will be notified when a chunk is restored.
     */
    fun addRestoreListener(listener: RestoreListener)

    /**
     * Unregisters a previously registered restore listener.
     */
    fun removeRestoreListener(listener: RestoreListener)
}
