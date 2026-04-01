package org.monolith.surveyor.api.handle

import org.monolith.surveyor.api.WorldId

/**
 * Opaque handle to a region file.
 */
interface RegionHandle {
    val worldId: WorldId
    val regionX: Int
    val regionZ: Int
}