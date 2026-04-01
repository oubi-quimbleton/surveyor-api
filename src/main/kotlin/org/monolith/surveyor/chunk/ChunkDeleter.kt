package org.monolith.surveyor.chunk

import org.monolith.surveyor.api.ChunkCoordinate
import org.monolith.surveyor.api.WorldId

/**
 * Internal abstraction for deleting chunks at the storage level.
 */
internal interface ChunkDeleter {
    fun deleteChunk(worldId: WorldId, coordinate: ChunkCoordinate)
}
