package org.monolith.surveyor.api.handle

import org.monolith.surveyor.api.WorldId

/**
 * Opaque handle to a chunk's data.
 *
 * Callers should not depend on its internal representation.
 */
interface ChunkHandle {
    val worldId: WorldId?
    val x: Int
    val z: Int
}