package org.monolith.surveyor.region.impl

import org.monolith.surveyor.api.WorldId
import org.monolith.surveyor.api.handle.RegionHandle
import org.monolith.surveyor.region.RegionEnumerator
import org.monolith.surveyor.io.RegionLocator

/**
 * Default implementation that enumerates region files from the filesystem.
 */
internal class DefaultRegionEnumerator(
    private val regionLocator: RegionLocator,
    private val regionHandleFactory: (WorldId, Int, Int) -> RegionHandle
) : RegionEnumerator {

    override fun listRegions(worldId: WorldId): List<RegionHandle> {
        val regionFiles = regionLocator.listRegions(worldId)

        return regionFiles.map { file ->
            val (regionX, regionZ) = regionLocator.parseRegionCoordinates(file)
            regionHandleFactory(worldId, regionX, regionZ)
        }
    }
}
