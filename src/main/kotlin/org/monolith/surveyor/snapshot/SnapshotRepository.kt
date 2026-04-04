package org.monolith.surveyor.snapshot

import net.kyori.adventure.nbt.CompoundBinaryTag
import org.monolith.surveyor.api.ChunkCoordinate
import org.monolith.surveyor.api.WorldId
import java.time.Instant

/**
 * Storage abstraction for snapshots.
 *
 * A snapshot is:
 * - identified by an ID
 * - associated with a source world
 * - contains a set of chunk NBT blobs + their coordinates
 */
interface SnapshotRepository {

    data class SnapshotChunk(
        val coordinate: ChunkCoordinate,
        val nbt: CompoundBinaryTag
    )

    data class SnapshotEntry(
        val id: String,
        val worldId: WorldId,
        val label: String?,
        val createdAt: Instant,
        val chunkCount: Int
    )

    /**
     * Stores a new snapshot and returns its ID.
     */
    fun saveSnapshot(
        worldId: WorldId,
        label: String?,
        chunks: Collection<SnapshotChunk>
    ): String

    /**
     * Lists all snapshots for a given world.
     */
    fun listSnapshots(worldId: WorldId): List<SnapshotEntry>

    /**
     * Loads all chunks for the given snapshot ID.
     */
    fun loadSnapshotChunks(id: String): List<SnapshotChunk>

    /**
     * Loads metadata for a snapshot ID, or null if not found.
     */
    fun loadSnapshotEntry(id: String): SnapshotEntry?

    /**
     * Deletes a snapshot. Returns true if it existed.
     */
    fun deleteSnapshot(id: String): Boolean
}
