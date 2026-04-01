package org.monolith.surveyor.api.access

import org.monolith.surveyor.api.ChunkCoordinate
import org.monolith.surveyor.api.handle.RegionHandle
import org.monolith.surveyor.api.WorldId

/**
 * Region-level access for bulk operations and low-level workflows.
 */
interface RegionAccess {

    /**
     * Returns a handle to a region containing the given chunk coordinates.
     */
    fun getRegion(worldId: WorldId, regionX: Int, regionZ: Int): RegionHandle?

    /**
     * Lists all regions known for a given world.
     */
    fun listRegions(worldId: WorldId): List<RegionHandle>
}
