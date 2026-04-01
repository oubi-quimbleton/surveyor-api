package org.monolith.surveyor.chunk

import org.monolith.surveyor.api.ChunkCoordinate
import org.monolith.surveyor.api.WorldId

/**
 * High-level operation for moving or copying chunks between worlds.
 *
 * This composes [ChunkExporter] and [ChunkImporter].
 */
interface ChunkTransplanter {

    /**
     * Copies a chunk from one world to another.
     *
     * @param sourceWorld Source world identifier.
     * @param source Source chunk coordinates.
     * @param targetWorld Target world identifier.
     * @param target Target chunk coordinates.
     * @param deleteSource Whether to delete the source chunk after transplant.
     */
    fun transplantChunk(
        sourceWorld: WorldId,
        source: ChunkCoordinate,
        targetWorld: WorldId,
        target: ChunkCoordinate,
        deleteSource: Boolean = false
    )
}
