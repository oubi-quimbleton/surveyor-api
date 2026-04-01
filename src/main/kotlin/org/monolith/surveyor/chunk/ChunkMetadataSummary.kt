package org.monolith.surveyor.chunk

/**
 * Simple summary of chunk metadata.
 */
data class ChunkMetadataSummary(
    val hasStructures: Boolean,
    val hasHeightmaps: Boolean,
    val biomeCount: Int,
    val dataVersion: Int?
)
