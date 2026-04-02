package org.monolith.surveyor.region

import org.monolith.surveyor.api.WorldId
import org.monolith.surveyor.api.handle.RegionHandle

/**
 * Provides indexed access to region handles by region coordinates.
 */
interface RegionIndex {

    /**
     * Returns a handle to the region at the given region coordinates, if it exists.
     */
    fun getRegion(worldId: WorldId, regionX: Int, regionZ: Int): RegionHandle?
}
