package org.monolith.surveyor.chunk

import org.monolith.surveyor.api.ChunkCoordinate
import org.monolith.surveyor.api.handle.ChunkHandle
import org.monolith.surveyor.api.WorldId
import org.monolith.surveyor.io.RegionLocator
import org.monolith.surveyor.nbt.RegionReader

/**
 * Exports chunk data from a world by reading directly from region files.
 */
interface ChunkExporter {

    /**
     * Reads a chunk from the given world and returns a handle to its data.
     *
     * @param worldId Source world identifier.
     * @param coordinate Chunk coordinates.
     * @return A [ChunkHandle] if the chunk exists, or null otherwise.
     */
    fun exportChunk(
        worldId: WorldId,
        coordinate: ChunkCoordinate
    ): ChunkHandle?
}

/**
 * Default NBT/region-based implementation of [ChunkExporter].
 */
internal class DefaultChunkExporter(
    private val regionLocator: RegionLocator,
    private val regionReader: RegionReader
) : ChunkExporter {

    override fun exportChunk(
        worldId: WorldId,
        coordinate: ChunkCoordinate
    ): ChunkHandle? {
        // 1. Locate region file
        val regionPath = regionLocator.locateRegion(worldId, coordinate) ?: return null

        // 2. Read raw NBT for the chunk
        val nbt = regionReader.readChunk(regionPath, coordinate) ?: return null

        // 3. Normalize NBT if needed (strip runtime data, etc.) – later
        return NbtBackedChunkHandle(worldId, coordinate, nbt)
    }
}
