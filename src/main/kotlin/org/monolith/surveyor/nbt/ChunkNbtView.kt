package org.monolith.surveyor.nbt

import net.kyori.adventure.nbt.CompoundBinaryTag

/**
 * Structured view over a chunk's NBT data.
 */
interface ChunkNbtView {

    /**
     * The root chunk NBT tag.
     */
    val root: CompoundBinaryTag

    /**
     * The chunk's DataVersion, if present.
     */
    val dataVersion: Int?

    /**
     * The chunk's Heightmaps compound, if present.
     */
    val heightmaps: CompoundBinaryTag?

    /**
     * The chunk's Structures compound, if present.
     */
    val structures: CompoundBinaryTag?

    /**
     * The chunk's Biomes container (usually inside Level/sections), if present.
     */
    val biomesRoot: CompoundBinaryTag?
}
