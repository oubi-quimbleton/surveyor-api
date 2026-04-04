package org.monolith.surveyor.nbt

import net.kyori.adventure.nbt.CompoundBinaryTag
import org.monolith.surveyor.io.FileHandle

/**
 * Reads chunk NBT data from a region file.
 *
 * Coordinates are local to the region (0–31).
 */
interface RegionReader {

    /**
     * Reads the chunk at the given local coordinates from the region file, or null if it is not present.
     */
    fun readChunk(regionFile: FileHandle, localChunkX: Int, localChunkZ: Int): CompoundBinaryTag?
}
