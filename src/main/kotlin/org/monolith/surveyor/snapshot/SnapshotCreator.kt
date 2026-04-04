package org.monolith.surveyor.snapshot

import org.monolith.surveyor.api.ChunkCoordinate
import org.monolith.surveyor.api.WorldId

/**
 * Creates snapshots from existing worlds.
 */
interface SnapshotCreator {

    /**
     * Creates a snapshot of the given [chunks] in [worldId], with an optional [label].
     *
     * Returns the snapshot ID.
     */
    fun createSnapshot(
        worldId: WorldId,
        chunks: Collection<ChunkCoordinate>,
        label: String? = null
    ): String
}
