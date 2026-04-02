package org.monolith.surveyor.io.impl

import org.monolith.surveyor.api.WorldId
import org.monolith.surveyor.io.FileHandle
import org.monolith.surveyor.io.RegionLocator
import java.nio.file.Files
import java.nio.file.Path

/**
 * Default RegionLocator implementation that uses a root worlds directory and
 * the standard Minecraft region folder layout.
 *
 * World path: <worldsRoot>/<worldId.id>
 * Region path: <worldsRoot>/<worldId.id>/region
 * Region file: r.<regionX>.<regionZ>.mca
 */
internal class DefaultRegionLocator(
    private val worldsRoot: Path
) : RegionLocator {

    override fun regionDirectory(worldId: WorldId): Path =
        worldsRoot.resolve(worldId.id).resolve("region")

    override fun locateRegionFile(worldId: WorldId, regionX: Int, regionZ: Int): FileHandle? {
        val dir = regionDirectory(worldId)
        val file = dir.resolve(regionFileName(regionX, regionZ))
        return if (Files.exists(file)) DefaultFileHandle(file) else null
    }

    override fun listRegions(worldId: WorldId): List<FileHandle> {
        val dir = regionDirectory(worldId)
        if (!Files.isDirectory(dir)) return emptyList()

        return Files.list(dir).use { stream ->
            stream
                .filter { path -> Files.isRegularFile(path) && path.fileName.toString().endsWith(".mca") }
                .map { path -> DefaultFileHandle(path) }
                .toList()
        }
    }

    override fun parseRegionCoordinates(file: FileHandle): Pair<Int, Int> {
        val name = file.path.fileName.toString()
        // Expected format: r.<x>.<z>.mca
        if (!name.startsWith("r.") || !name.endsWith(".mca")) {
            throw IllegalArgumentException("Not a valid region filename: $name")
        }

        val core = name.removePrefix("r.").removeSuffix(".mca")
        val parts = core.split('.')
        if (parts.size != 2) {
            throw IllegalArgumentException("Not a valid region filename: $name")
        }

        val regionX = parts[0].toIntOrNull()
            ?: throw IllegalArgumentException("Invalid region X in filename: $name")
        val regionZ = parts[1].toIntOrNull()
            ?: throw IllegalArgumentException("Invalid region Z in filename: $name")

        return regionX to regionZ
    }

    private fun regionFileName(regionX: Int, regionZ: Int): String =
        "r.$regionX.$regionZ.mca"
}
