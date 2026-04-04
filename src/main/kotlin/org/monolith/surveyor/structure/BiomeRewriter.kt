package org.monolith.surveyor.structure

import net.kyori.adventure.nbt.CompoundBinaryTag

/**
 * Rewrites biome data in a chunk NBT.
 */
interface BiomeRewriter {

    /**
     * Rewrites biome data in [root].
     *
     * Implementations may:
     * - remap biome IDs
     * - normalize container layout
     * - optionally blend edges
     */
    fun rewrite(root: CompoundBinaryTag): CompoundBinaryTag
}
