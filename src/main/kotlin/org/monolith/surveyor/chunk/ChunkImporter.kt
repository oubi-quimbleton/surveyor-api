package org.monolith.surveyor.chunk

import org.monolith.surveyor.api.ChunkCoordinate
import org.monolith.surveyor.api.handle.ChunkHandle
import org.monolith.surveyor.api.WorldId

/**
 * Imports chunk data into a target world by writing directly to region files.
 */
interface ChunkImporter {

    /**
     * Imports the given chunk into the target world at the given coordinates.
     *
     * @param targetWorld Target world identifier.
     * @param target Target chunk coordinates.
     * @param sourceChunk Chunk handle containing NBT to import.
     * @param overwrite Whether to overwrite existing chunk data.
     */
    fun importChunk(
        targetWorld: WorldId,
        target: ChunkCoordinate,
        sourceChunk: ChunkHandle,
        overwrite: Boolean = true
    )
}
