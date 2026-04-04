package org.monolith.surveyor.nbt

import net.kyori.adventure.nbt.CompoundBinaryTag
import org.monolith.surveyor.io.FileHandle

/**
 * Writes chunk NBT data into a region file.
 *
 * Coordinates are local to the region (0–31).
 */
interface RegionWriter {

    /**
     * Writes or replaces the chunk at the given local coordinates in the region file.
     */
    fun writeChunk(regionFile: FileHandle, localChunkX: Int, localChunkZ: Int, nbt: CompoundBinaryTag)
}
