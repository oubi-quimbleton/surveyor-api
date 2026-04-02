package org.monolith.surveyor.nbt

import net.kyori.adventure.nbt.CompoundBinaryTag
import org.monolith.surveyor.api.handle.ChunkHandle

/**
 * ChunkHandle implementation backed by Kyori NBT.
 */
interface NbtBackedChunkHandle : ChunkHandle {

    /**
     * The raw chunk NBT.
     */
    val nbt: CompoundBinaryTag

    /**
     * A structured view over the chunk NBT.
     */
    val view: ChunkNbtView
}
