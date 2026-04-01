package org.monolith.surveyor.chunk.impl

import org.monolith.surveyor.api.ChunkCoordinate
import org.monolith.surveyor.api.WorldId
import org.monolith.surveyor.chunk.ChunkDeleter
import org.monolith.surveyor.chunk.ChunkExporter
import org.monolith.surveyor.chunk.ChunkImporter
import org.monolith.surveyor.chunk.ChunkTransplanter

/**
 * Default implementation of [ChunkTransplanter] using exporter/importer.
 */
internal class DefaultChunkTransplanter(
    private val exporter: ChunkExporter,
    private val importer: ChunkImporter,
    private val chunkDeleter: ChunkDeleter
) : ChunkTransplanter {

    override fun transplantChunk(
        sourceWorld: WorldId,
        source: ChunkCoordinate,
        targetWorld: WorldId,
        target: ChunkCoordinate,
        deleteSource: Boolean
    ) {
        val handle = exporter.exportChunk(sourceWorld, source) ?: return

        importer.importChunk(
            targetWorld = targetWorld,
            target = target,
            sourceChunk = handle,
            overwrite = true
        )

        if (deleteSource) {
            chunkDeleter.deleteChunk(sourceWorld, source)
        }
    }
}
