package org.monolith.surveyor.nbt.impl

import net.kyori.adventure.nbt.CompoundBinaryTag
import net.kyori.adventure.nbt.IntBinaryTag
import org.monolith.surveyor.nbt.ChunkNbtView

/**
 * Default ChunkNbtView implementation for standard chunk NBT layout.
 */
internal class DefaultChunkNbtView(
    override val root: CompoundBinaryTag
) : ChunkNbtView {

    override val dataVersion: Int? =
        (root.get("DataVersion") as? IntBinaryTag)?.value()

    override val heightmaps: CompoundBinaryTag? =
        (root.get("Heightmaps") as? CompoundBinaryTag)
            ?: (root.get("Level") as? CompoundBinaryTag)?.get("Heightmaps") as? CompoundBinaryTag

    override val structures: CompoundBinaryTag? =
        (root.get("Structures") as? CompoundBinaryTag)
            ?: (root.get("Level") as? CompoundBinaryTag)?.get("Structures") as? CompoundBinaryTag

    override val biomesRoot: CompoundBinaryTag? =
        (root.get("Level") as? CompoundBinaryTag)
            ?.get("Biomes") as? CompoundBinaryTag
}
