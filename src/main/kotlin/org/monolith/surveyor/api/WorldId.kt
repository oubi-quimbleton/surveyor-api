package org.monolith.surveyor.api

import java.util.UUID

/**
 * Logical identifier for a world.
 *
 * This can be mapped to a Paper/NeoForge world internally.
 */
@JvmInline
value class WorldId(val id: String) {
    companion object {
        fun fromUuid(uuid: UUID): WorldId = WorldId(uuid.toString())
    }
}
