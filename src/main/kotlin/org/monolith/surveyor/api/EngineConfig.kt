package org.monolith.surveyor.api

/**
 * Configuration for the Surveyor Engine.
 *
 * This should remain small and stable.
 */
data class EngineConfig(
    val maxConcurrentIO: Int = 4,
    val allowActiveWorldWrites: Boolean = false,
    val snapshotRootPath: String? = null,
    val verboseLogging: Boolean = false
)
