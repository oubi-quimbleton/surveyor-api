package org.monolith.surveyor.api

import org.monolith.surveyor.api.access.ChunkAccess
import org.monolith.surveyor.api.access.RegionAccess
import org.monolith.surveyor.api.access.SnapshotAccess

/**
 * Entry point for the Surveyor Engine.
 *
 * This is the only type most consumers should need to interact with directly.
 */
interface SurveyorEngine {

    val config: EngineConfig

    /**
     * Provides access to chunk-level operations.
     */
    val chunks: ChunkAccess

    /**
     * Provides access to region-level operations.
     */
    val regions: RegionAccess

    /**
     * Provides access to snapshot operations.
     */
    val snapshots: SnapshotAccess

    /**
     * Shuts down the engine and releases any resources.
     */
    fun shutdown()

    companion object {
        /**
         * Creates a new engine instance with the given configuration.
         */
        fun create(config: EngineConfig): SurveyorEngine {
            return DefaultSurveyorEngine(config)
        }
    }
}
