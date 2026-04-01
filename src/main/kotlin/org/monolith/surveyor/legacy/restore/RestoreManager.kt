package org.monolith.surveyor.legacy.restore

import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.block.TileState
import org.monolith.surveyor.legacy.api.RestoreListener
import org.monolith.surveyor.legacy.restore.block.BlockEntityRestorer
import org.monolith.surveyor.legacy.restore.entity.EntityRestorer
import kotlin.system.measureTimeMillis

internal class RestoreManager(
    private val sourceWorld: World
) {

    private val blockEntityRestorer = BlockEntityRestorer()
    private val entityRestorer = EntityRestorer()
    private val listeners = mutableSetOf<RestoreListener>()

    fun restoreAt(block: Block) {
        val chunk = block.chunk
        restoreChunk(chunk)
    }

    fun restoreChunk(targetChunk: Chunk) {
        if (!targetChunk.isLoaded) return

        val sourceChunk = sourceWorld.getChunkAt(targetChunk.x, targetChunk.z, true)

        val duration = measureTimeMillis {
            restoreBiomes(targetChunk, sourceChunk)
            restoreBlocks(targetChunk, sourceChunk)
            entityRestorer.restoreEntities(sourceChunk, targetChunk)
            targetChunk.world.refreshChunk(targetChunk.x, targetChunk.z)
        }

        listeners.forEach { it.onChunkRestored(targetChunk, duration) }
    }

    fun restoreRegion(min: Location, max: Location) {
        require(min.world == max.world) { "Region must be in a single world" }
        val world = min.world ?: return

        val minChunkX = min.blockX shr 4
        val maxChunkX = max.blockX shr 4
        val minChunkZ = min.blockZ shr 4
        val maxChunkZ = max.blockZ shr 4

        for (cx in minChunkX..maxChunkX) {
            for (cz in minChunkZ..maxChunkZ) {
                val chunk = world.getChunkAt(cx, cz)
                restoreChunk(chunk)
            }
        }
    }

    fun addRestoreListener(listener: RestoreListener) {
        listeners += listener
    }

    fun removeRestoreListener(listener: RestoreListener) {
        listeners -= listener
    }

    private fun restoreBiomes(target: Chunk, source: Chunk) {
        val world = target.world
        val baseX = target.x shl 4
        val baseZ = target.z shl 4
        val minY = world.minHeight
        val maxY = world.maxHeight

        for (x in 0..15) {
            for (z in 0..15) {
                for (y in minY until maxY step 4) {
                    val biome = source.getBlock(x, y, z).biome
                    world.setBiome(baseX + x, y, baseZ + z, biome)
                }
            }
        }
    }

    private fun restoreBlocks(target: Chunk, source: Chunk) {
        val world = target.world
        val minY = world.minHeight
        val maxY = world.maxHeight

        for (x in 0..15) {
            for (z in 0..15) {
                for (y in minY until maxY) {
                    val sourceBlock = source.getBlock(x, y, z)
                    val targetBlock = target.getBlock(x, y, z)
                    copyBlock(sourceBlock, targetBlock)
                }
            }
        }
    }

    private fun copyBlock(source: Block, target: Block) {
        val sType = source.type
        val tType = target.type

        if (sType == Material.AIR && tType == Material.AIR) return

        if (sType == Material.AIR) {
            target.setType(Material.AIR, false)
            return
        }

        val sData = source.blockData
        val tData = target.blockData

        if (sType == tType && tData.matches(sData)) {
            restoreTileEntityIfNeeded(source, target)
            return
        }

        target.setBlockData(sData, false)
        restoreTileEntityIfNeeded(source, target)
    }

    private fun restoreTileEntityIfNeeded(source: Block, target: Block) {
        val sState = source.state
        val tState = target.state

        if (sState is TileState && tState is TileState) {
            blockEntityRestorer.restoreBlockEntity(sState, tState)
        }
    }
}
