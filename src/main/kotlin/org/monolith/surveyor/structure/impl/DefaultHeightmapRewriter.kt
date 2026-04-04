package org.monolith.surveyor.structure.impl

import net.kyori.adventure.nbt.CompoundBinaryTag
import org.monolith.surveyor.structure.HeightmapRewriter

/**
 * Default HeightmapRewriter implementation.
 *
 * This implementation is conservative and currently leaves heightmaps unchanged.
 * It is safe for most use cases, especially when the transplanted chunks already
 * contain valid heightmaps from their source world.
 *
 * A future implementation could:
 * - read block data from sections
 * - recompute heightmaps (WORLD_SURFACE, MOTION_BLOCKING, etc.)
 * - repack them into the correct long[] format
 */
internal class DefaultHeightmapRewriter : HeightmapRewriter {

    override fun rewrite(root: CompoundBinaryTag): CompoundBinaryTag {
        // No-op for now. Heightmaps from the source chunk are preserved.
        return root
    }
}
