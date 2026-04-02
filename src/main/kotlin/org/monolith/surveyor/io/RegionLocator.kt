package org.monolith.surveyor.io

import org.monolith.surveyor.api.WorldId
import java.nio.file.Path

/**
 * Locates and enumerates region files for a given world.
 *
 * This is a low-level, filesystem-oriented abstraction. It does not know about NBT.
 */
interface RegionLocator {

    /**
     * Returns the canonical region directory for the given world.
     */
    fun regionDirectory(worldId: WorldId): Path

    /**
     * Locates the region file for the given region coordinates, if it exists.
     */
    fun locateRegionFile(worldId: WorldId, regionX: Int, regionZ: Int): FileHandle?

    /**
     * Lists all region files for the given world.
     */
    fun listRegions(worldId: WorldId): List<FileHandle>

    /**
     * Parses region coordinates from a region file handle.
     *
     * Expected filename format: r.<regionX>.<regionZ>.mca
     */
    fun parseRegionCoordinates(file: FileHandle): Pair<Int, Int>
}
