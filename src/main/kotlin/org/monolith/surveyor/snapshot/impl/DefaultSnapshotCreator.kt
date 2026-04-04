package org.monolith.surveyor.snapshot.impl

import org.monolith.surveyor.api.ChunkCoordinate
import org.monolith.surveyor.api.WorldId
import org.monolith.surveyor.chunk.ChunkExporter
import org.monolith.surveyor.nbt.NbtBackedChunkHandle
import org.monolith.surveyor.snapshot.SnapshotCreator
import org.monolith.surveyor.snapshot.SnapshotRepository

/**
 * Default SnapshotCreator implementation.
 *
 * Uses ChunkExporter to read chunks and SnapshotRepository to persist them.
 */
internal class DefaultSnapshotCreator(
    private val exporter: ChunkExporter,
    private val repository: SnapshotRepository
) : SnapshotCreator {

    override fun createSnapshot(
        worldId: WorldId,
        chunks: Collection<ChunkCoordinate>,
        label: String?
    ): String {
        val snapshotChunks = mutableListOf<SnapshotRepository.SnapshotChunk>()

        for (coord in chunks) {
            val handle = exporter.exportChunk(worldId, coord) as? NbtBackedChunkHandle ?: continue
            snapshotChunks += SnapshotRepository.SnapshotChunk(
                coordinate = coord,
                nbt = handle.nbt
            )
        }

        return repository.saveSnapshot(worldId, label, snapshotChunks)
    }
}
