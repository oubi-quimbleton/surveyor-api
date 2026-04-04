package org.monolith.surveyor.chunk.impl

import org.monolith.surveyor.api.ChunkCoordinate
import org.monolith.surveyor.api.WorldId
import org.monolith.surveyor.api.handle.ChunkHandle
import org.monolith.surveyor.chunk.ChunkExporter
import org.monolith.surveyor.io.RegionLocator
import org.monolith.surveyor.nbt.RegionReader

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
