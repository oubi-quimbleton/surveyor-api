package org.monolith.surveyor.chunk

import org.monolith.surveyor.api.handle.ChunkHandle
import org.monolith.surveyor.nbt.ChunkNbtView

/**
 * Provides metadata extraction and manipulation for chunks.
 *
 * This operates on NBT-level representations, not Bukkit/Paper chunks.
 */
interface ChunkMetadata {

    /**
     * Extracts a summary of metadata for the given chunk.
     */
    fun describe(chunk: ChunkHandle): ChunkMetadataSummary
}

/**
 * Simple summary of chunk metadata.
 */
data class ChunkMetadataSummary(
    val hasStructures: Boolean,
    val hasHeightmaps: Boolean,
    val biomeCount: Int,
    val dataVersion: Int?
)

/**
 * Default implementation of [ChunkMetadata] using NBT views.
 */
internal class DefaultChunkMetadata(
    private val nbtViewFactory: (ChunkHandle) -> ChunkNbtView
) : ChunkMetadata {

    override fun describe(chunk: ChunkHandle): ChunkMetadataSummary {
        val view = nbtViewFactory(chunk)

        val hasStructures = view.hasStructures()
        val hasHeightmaps = view.hasHeightmaps()
        val biomeCount = view.biomeCount()
        val dataVersion = view.dataVersion()

        return ChunkMetadataSummary(
            hasStructures = hasStructures,
            hasHeightmaps = hasHeightmaps,
            biomeCount = biomeCount,
            dataVersion = dataVersion
        )
    }
}
