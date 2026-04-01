package org.monolith.surveyor.api.access

import org.monolith.surveyor.api.ChunkCoordinate
import org.monolith.surveyor.api.handle.SnapshotHandle
import org.monolith.surveyor.api.WorldId

/**
 * High-level API for working with snapshots.
 */
interface SnapshotAccess {

    /**
     * Creates a snapshot of the given chunks in a world.
     *
     * @param worldId Source world.
     * @param chunks List of chunk coordinates to snapshot.
     */
    fun createSnapshot(
        worldId: WorldId,
        chunks: List<ChunkCoordinate>,
        label: String? = null
    ): SnapshotHandle

    /**
     * Restores a snapshot into the given world.
     */
    fun restoreSnapshot(
        worldId: WorldId,
        snapshot: SnapshotHandle,
        overwrite: Boolean = true
    )

    /**
     * Lists all known snapshots.
     */
    fun listSnapshots(): List<SnapshotHandle>
}
