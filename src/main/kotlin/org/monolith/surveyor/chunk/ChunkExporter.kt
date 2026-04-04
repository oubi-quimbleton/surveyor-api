package org.monolith.surveyor.chunk

import org.monolith.surveyor.api.ChunkCoordinate
import org.monolith.surveyor.api.handle.ChunkHandle
import org.monolith.surveyor.api.WorldId

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
