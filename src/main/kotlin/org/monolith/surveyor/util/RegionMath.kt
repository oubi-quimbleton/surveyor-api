package org.monolith.surveyor.util

import org.monolith.surveyor.api.ChunkCoordinate

/**
 * Utilities for region file coordinate math.
 *
 * Region files are 32×32 chunks.
 */
object RegionMath {

    const val REGION_SIZE = 32

    /**
     * Converts chunk coordinate to region coordinate.
     */
    fun chunkToRegion(chunk: Int): Int =
        ChunkMath.floorDiv(chunk, REGION_SIZE)

    /**
     * Converts chunk coordinates to region coordinates.
     */
    fun chunkToRegionCoord(coord: ChunkCoordinate): Pair<Int, Int> =
        chunkToRegion(coord.x) to chunkToRegion(coord.z)

    /**
     * Returns the index inside a region file (0–1023).
     */
    fun regionIndex(chunkX: Int, chunkZ: Int): Int {
        val rx = chunkX and (REGION_SIZE - 1)
        val rz = chunkZ and (REGION_SIZE - 1)
        return rz * REGION_SIZE + rx
    }
}
