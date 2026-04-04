package org.monolith.surveyor.structure

import net.kyori.adventure.nbt.CompoundBinaryTag

/**
 * Rewrites heightmap data in a chunk NBT.
 */
interface HeightmapRewriter {

    /**
     * Rewrites heightmaps in [root].
     *
     * Implementations may recompute heightmaps from block data or normalize layout.
     */
    fun rewrite(root: CompoundBinaryTag): CompoundBinaryTag
}
