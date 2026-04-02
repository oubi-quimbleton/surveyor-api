package org.monolith.surveyor.region

import org.monolith.surveyor.api.WorldId
import org.monolith.surveyor.api.handle.RegionHandle

/**
 * Enumerates region files for a given world.
 */
interface RegionEnumerator {

    /**
     * Lists all regions known for the given world.
     */
    fun listRegions(worldId: WorldId): List<RegionHandle>
}
