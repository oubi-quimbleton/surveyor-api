package org.monolith.surveyor.snapshot.impl

import org.monolith.surveyor.api.ChunkCoordinate
import org.monolith.surveyor.api.WorldId
import org.monolith.surveyor.chunk.ChunkImporter
import org.monolith.surveyor.nbt.NbtBackedChunkHandle
import org.monolith.surveyor.nbt.impl.DefaultNbtBackedChunkHandle
import org.monolith.surveyor.snapshot.SnapshotRestorer
import org.monolith.surveyor.snapshot.SnapshotRepository

/**
 * Default SnapshotRestorer implementation.
 *
 * Loads snapshot chunks from the repository and imports them into the target world.
 */
internal class DefaultSnapshotRestorer(
    private val importer: ChunkImporter,
    private val repository: SnapshotRepository
) : SnapshotRestorer {

    override fun restoreSnapshot(
        snapshotId: String,
        targetWorld: WorldId,
        offset: ChunkCoordinate
    ) {
        val chunks = repository.loadSnapshotChunks(snapshotId)
        if (chunks.isEmpty()) return

        for (chunk in chunks) {
            val targetCoord = ChunkCoordinate(
                x = chunk.coordinate.x + offset.x,
                z = chunk.coordinate.z + offset.z
            )

            val handle: NbtBackedChunkHandle = DefaultNbtBackedChunkHandle(
                worldId = targetWorld,
                coordinate = targetCoord,
                nbt = chunk.nbt
            )

            importer.importChunk(
                targetWorld = targetWorld,
                target = targetCoord,
                sourceChunk = handle,
                overwrite = true
            )
        }
    }
}
