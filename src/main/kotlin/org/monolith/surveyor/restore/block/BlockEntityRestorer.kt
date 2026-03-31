package org.monolith.surveyor.restore.block

import net.kyori.adventure.text.Component
import org.bukkit.block.*
import org.bukkit.block.sign.Side
import org.bukkit.loot.Lootable

internal class BlockEntityRestorer {

    fun restoreBlockEntity(source: TileState, target: TileState) {
        source.persistentDataContainer.copyTo(target.persistentDataContainer, true)

        when (source) {
            is Container -> restoreContainer(source, target as Container)
            is ShulkerBox -> restoreShulker(source, target as ShulkerBox)
            is Furnace -> restoreFurnace(source, target as Furnace)
            is BrewingStand -> restoreBrewingStand(source, target as BrewingStand)
            is Sign -> restoreSign(source, target as Sign)
            is CreatureSpawner -> restoreSpawner(source, target as CreatureSpawner)
            is CommandBlock -> restoreCommandBlock()
            is Lectern -> restoreLectern(source, target as Lectern)
            is Jukebox -> restoreJukebox(source, target as Jukebox)
        }

        target.update(true, false)
    }

    private fun restoreContainer(source: Container, target: Container) {
        val lootable = source as? Lootable
        val targetLootable = target as? Lootable

        if (lootable?.lootTable != null && targetLootable != null) {
            targetLootable.lootTable = lootable.lootTable
            targetLootable.seed = lootable.seed
        } else {
            val src = source.inventory.contents
            val dst = Array(src.size) { i -> src[i]?.clone() }
            target.inventory.contents = dst
        }

        setBlockNameCompat(target, getBlockNameCompat(source))
    }

    private fun restoreShulker(source: ShulkerBox, target: ShulkerBox) {
        val src = source.inventory.contents
        val dst = Array(src.size) { i -> src[i]?.clone() }
        target.inventory.contents = dst
        setBlockNameCompat(target, getBlockNameCompat(source))
    }

    private fun restoreFurnace(source: Furnace, target: Furnace) {
        target.burnTime = source.burnTime
        target.cookTime = source.cookTime
        target.cookTimeTotal = source.cookTimeTotal
        setBlockNameCompat(target, getBlockNameCompat(source))
    }

    private fun restoreBrewingStand(source: BrewingStand, target: BrewingStand) {
        target.brewingTime = source.brewingTime
        target.fuelLevel = source.fuelLevel
        setBlockNameCompat(target, getBlockNameCompat(source))
    }

    private fun restoreSign(source: Sign, target: Sign) {
        for (side in Side.entries) {
            val sSide = source.getSide(side)
            val tSide = target.getSide(side)
            for (i in 0..3) {
                tSide.line(i, sSide.line(i))
            }
        }

        target.isWaxed = source.isWaxed

        @Suppress("DEPRECATION")
        target.isGlowingText = source.isGlowingText
    }

    private fun restoreSpawner(source: CreatureSpawner, target: CreatureSpawner) {
        target.spawnedType = source.spawnedType
        target.delay = source.delay
        target.minSpawnDelay = source.minSpawnDelay
        target.maxSpawnDelay = source.maxSpawnDelay
        target.spawnCount = source.spawnCount
        target.maxNearbyEntities = source.maxNearbyEntities
        target.requiredPlayerRange = source.requiredPlayerRange
        target.spawnRange = source.spawnRange
    }

    private fun restoreCommandBlock() {
        // No writable fields exposed by Bukkit; PDC already copied.
    }

    private fun restoreLectern(source: Lectern, target: Lectern) {
        val book = source.inventory.getItem(0)
        target.inventory.setItem(0, book?.clone())
        target.page = source.page
    }

    private fun restoreJukebox(source: Jukebox, target: Jukebox) {
        target.setRecord(source.record.clone())
    }

    @Suppress("DEPRECATION")
    private fun getBlockNameCompat(container: Container): Component? {
        return try {
            container.customName() // Paper API
        } catch (ex: NoSuchMethodError) {
            container.getCustomName()?.let { Component.text(it) } // Bukkit/ArcLight
        }
    }

    @Suppress("DEPRECATION")
    private fun setBlockNameCompat(container: Container, name: Component?) {
        try {
            container.customName(name) // Paper API
        } catch (ex: NoSuchMethodError) {
            container.setCustomName(name?.toString()) // Bukkit/ArcLight
        }
    }
}
