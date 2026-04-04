package org.monolith.surveyor.util

import org.monolith.surveyor.api.ChunkCoordinate
import kotlin.math.floor

/**
 * Utilities for chunk/block coordinate conversions.
 */
object ChunkMath {

    /**
     * Converts a block coordinate to a chunk coordinate using floor division.
     */
    fun blockToChunk(block: Int): Int =
        floor(block / 16.0).toInt()

    /**
     * Converts chunk coordinate to block coordinate (chunk origin).
     */
    fun chunkToBlock(chunk: Int): Int =
        chunk * 16

    /**
     * Converts block coordinates to a ChunkCoordinate.
     */
    fun blockToChunkCoord(x: Int, z: Int): ChunkCoordinate =
        ChunkCoordinate(blockToChunk(x), blockToChunk(z))

    /**
     * Java-style floor division for negative coordinates.
     */
    fun floorDiv(a: Int, b: Int): Int {
        val r = a / b
        return if ((a xor b) < 0 && r * b != a) r - 1 else r
    }
}
