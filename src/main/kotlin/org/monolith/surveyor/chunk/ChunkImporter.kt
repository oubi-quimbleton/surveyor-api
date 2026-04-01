package org.monolith.surveyor.chunk

import org.monolith.surveyor.api.ChunkCoordinate
import org.monolith.surveyor.api.handle.ChunkHandle
import org.monolith.surveyor.api.WorldId
import org.monolith.surveyor.io.RegionLocator
import org.monolith.surveyor.nbt.RegionWriter
import org.monolith.surveyor.structure.BiomeRewriter
import org.monolith.surveyor.structure.HeightmapRewriter
import org.monolith.surveyor.structure.StructureRewriter

/**
 * Imports chunk data into a target world by writing directly to region files.
 */
interface ChunkImporter {

    /**
     * Imports the given chunk into the target world at the given coordinates.
     *
     * @param targetWorld Target world identifier.
     * @param target Target chunk coordinates.
     * @param sourceChunk Chunk handle containing NBT to import.
     * @param overwrite Whether to overwrite existing chunk data.
     */
    fun importChunk(
        targetWorld: WorldId,
        target: ChunkCoordinate,
        sourceChunk: ChunkHandle,
        overwrite: Boolean = true
    )
}

/**
 * Default NBT/region-based implementation of [ChunkImporter].
 */
internal class DefaultChunkImporter(
    private val regionLocator: RegionLocator,
    private val regionWriter: RegionWriter,
    private val structureRewriter: StructureRewriter,
    private val biomeRewriter: BiomeRewriter,
    private val heightmapRewriter: HeightmapRewriter
) : ChunkImporter {

    override fun importChunk(
        targetWorld: WorldId,
        target: ChunkCoordinate,
        sourceChunk: ChunkHandle,
        overwrite: Boolean
    ) {
        // 1. Locate target region file
        val regionPath = regionLocator.locateRegion(targetWorld, target)

        // 2. Obtain raw NBT from the source chunk (implementation detail)
        val nbt = (sourceChunk as? NbtBackedChunkHandle)
            ?: error("Unsupported ChunkHandle implementation: ${sourceChunk::class.qualifiedName}")

        val mutableNbt = nbt.nbt.deepCopy()

        // 3. Rewrite metadata for the target world/position
        structureRewriter.rewriteStructures(mutableNbt, targetWorld, target)
        biomeRewriter.rewriteBiomes(mutableNbt, targetWorld)
        heightmapRewriter.rewriteHeightmaps(mutableNbt)

        // 4. Write the chunk NBT into the target region file
        regionWriter.writeChunk(regionPath, target, mutableNbt, overwrite)
    }
}
