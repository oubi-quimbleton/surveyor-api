package org.monolith.surveyor.nbt.impl

import net.kyori.adventure.nbt.CompoundBinaryTag
import org.monolith.surveyor.api.ChunkCoordinate
import org.monolith.surveyor.api.WorldId
import org.monolith.surveyor.api.handle.ChunkHandle
import org.monolith.surveyor.nbt.ChunkNbtView
import org.monolith.surveyor.nbt.NbtBackedChunkHandle

/**
 * Default NbtBackedChunkHandle implementation.
 */
internal class DefaultNbtBackedChunkHandle(
    override val worldId: WorldId,
    override val coordinate: ChunkCoordinate,
    override val nbt: CompoundBinaryTag
) : NbtBackedChunkHandle, ChunkHandle {

    override val view: ChunkNbtView = DefaultChunkNbtView(nbt)

    override fun toString(): String =
        "NbtBackedChunkHandle(worldId=$worldId, coordinate=$coordinate)"
}
