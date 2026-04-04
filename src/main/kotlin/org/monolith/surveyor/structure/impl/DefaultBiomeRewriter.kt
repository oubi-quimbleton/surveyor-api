package org.monolith.surveyor.structure.impl

import net.kyori.adventure.nbt.CompoundBinaryTag
import org.monolith.surveyor.structure.BiomeRewriter

/**
 * Default BiomeRewriter implementation.
 *
 * This implementation is conservative and currently acts as a no-op, but it provides
 * a single place to plug in biome remapping or normalization logic later.
 */
internal class DefaultBiomeRewriter : BiomeRewriter {

    override fun rewrite(root: CompoundBinaryTag): CompoundBinaryTag {
        // For now, do not alter biomes. This is a safe default and avoids
        // breaking modded biome IDs or custom biome containers.
        //
        // Future extensions could:
        // - remap biome IDs
        // - normalize container layout
        // - blend edges between transplanted and native terrain
        return root
    }
}
