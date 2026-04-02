package org.monolith.surveyor.region.impl

import org.monolith.surveyor.api.WorldId
import org.monolith.surveyor.api.handle.RegionHandle
import org.monolith.surveyor.region.RegionIndex
import org.monolith.surveyor.io.RegionLocator

/**
 * Default implementation that resolves regions by coordinates using the filesystem.
 */
internal class DefaultRegionIndex(
    private val regionLocator: RegionLocator,
    private val regionHandleFactory: (WorldId, Int, Int) -> RegionHandle
) : RegionIndex {

    override fun getRegion(worldId: WorldId, regionX: Int, regionZ: Int): RegionHandle? {
        val file = regionLocator.locateRegionFile(worldId, regionX, regionZ) ?: return null
        // Existence is already implied by locateRegionFile; we just wrap it.
        return regionHandleFactory(worldId, regionX, regionZ)
    }
}
