package org.monolith.surveyor.structure

import net.kyori.adventure.nbt.CompoundBinaryTag

/**
 * Rewrites the Structures section of a chunk NBT when the chunk is moved.
 *
 * Offsets are in block coordinates.
 */
interface StructureRewriter {

    /**
     * Rewrites structure data in [root] to account for a move by [blockOffsetX], [blockOffsetZ].
     *
     * Implementations should:
     * - adjust bounding boxes
     * - adjust structure piece positions
     * - adjust any stored chunk coordinates
     */
    fun rewrite(root: CompoundBinaryTag, blockOffsetX: Int, blockOffsetZ: Int): CompoundBinaryTag
}
