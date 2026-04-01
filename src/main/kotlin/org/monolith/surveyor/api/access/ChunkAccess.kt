package org.monolith.surveyor.api.access

import org.monolith.surveyor.api.ChunkCoordinate
import org.monolith.surveyor.api.handle.ChunkHandle
import org.monolith.surveyor.api.WorldId

/**
 * High-level API for reading and writing chunk data.
 *
 * This is intentionally world-agnostic: callers provide identifiers.
 */
interface ChunkAccess {

    /**
     * Reads a chunk's raw representation from the given world.
     *
     * @param worldId Logical identifier for the world (implementation-defined).
     * @param coordinate Chunk coordinate.
     */
    fun readChunk(worldId: WorldId, coordinate: ChunkCoordinate): ChunkHandle?

    /**
     * Writes a chunk into the given world.
     *
     * @param worldId Target world identifier.
     * @param chunk Chunk handle previously obtained from this or another engine.
     * @param overwrite Whether to overwrite existing chunk data.
     */
    fun writeChunk(worldId: WorldId, chunk: ChunkHandle, overwrite: Boolean = true)
}
