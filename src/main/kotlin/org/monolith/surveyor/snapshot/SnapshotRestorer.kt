package org.monolith.surveyor.snapshot

import org.monolith.surveyor.api.ChunkCoordinate
import org.monolith.surveyor.api.WorldId

/**
 * Restores snapshots into worlds.
 */
interface SnapshotRestorer {

    /**
     * Restores the snapshot [snapshotId] into [targetWorld].
     *
     * [offset] is applied in chunk coordinates to all stored chunks.
     */
    fun restoreSnapshot(
        snapshotId: String,
        targetWorld: WorldId,
        offset: ChunkCoordinate = ChunkCoordinate(0, 0)
    )
}
