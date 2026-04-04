package org.monolith.surveyor.chunk.impl

import org.monolith.surveyor.api.handle.ChunkHandle
import org.monolith.surveyor.chunk.ChunkMetadata
import org.monolith.surveyor.chunk.ChunkMetadataSummary

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
