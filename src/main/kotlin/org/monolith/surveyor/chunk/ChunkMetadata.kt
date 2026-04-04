package org.monolith.surveyor.chunk

import org.monolith.surveyor.api.handle.ChunkHandle

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
