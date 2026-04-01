package org.monolith.surveyor.legacy.restore.entity

import net.kyori.adventure.text.Component
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.World
import org.bukkit.attribute.Attribute
import org.bukkit.entity.*
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.persistence.PersistentDataType
import java.util.UUID

internal class EntityRestorer {

    private val entityUuidKey = NamespacedKey.fromString("surveyor:template_uuid")!!

    fun restoreEntities(sourceChunk: Chunk, targetChunk: Chunk) {
        if (!sourceChunk.isLoaded || !targetChunk.isLoaded) return

        // Remove dropped items (Item entities)
        targetChunk.entities
            .filterIsInstance<Item>()
            .forEach { it.remove() }

        val targetEntities = targetChunk.entities.toList()
        val sourceEntities = sourceChunk.entities.toList()

        for (source in sourceEntities) {
            if (!isRestorable(source)) continue

            val uuid = getOrCreateTemplateUUID(source)
            findEntityByUUID(targetEntities, uuid)?.remove()

            val copy = spawnEntityCopy(source, targetChunk.world, uuid)
            copyEntityData(source, copy)
        }
    }

    private fun isRestorable(entity: Entity): Boolean {

        // Only restore living mobs
        if (entity !is LivingEntity) return false

        // Never restore players
        if (entity is Player) return false

        // Skip pets (player-owned)
        if (entity is Tameable && entity.isTamed) return false

        // Skip named mobs (player-named or special)
        if (getNameCompat(entity) != null) return false

        // Skip leashed mobs (player-placed)
        if (entity is Mob && isLeashedCompat(entity)) return false

        // Skip armor stands, NPCs, etc.
        if (entity is ArmorStand) return false

        // Skip projectiles, XP orbs, etc.
        if (entity is Projectile || entity is ExperienceOrb) return false

        // Skip minecarts, boats, etc.
        if (entity is Vehicle) return false

        // Everything else is restorable (including modded mobs)
        return true
    }

    private fun getOrCreateTemplateUUID(entity: Entity): UUID {
        val container = entity.persistentDataContainer
        val existing = container.get(entityUuidKey, PersistentDataType.STRING)
        if (existing != null) return UUID.fromString(existing)

        val newId = UUID.randomUUID()
        container.set(entityUuidKey, PersistentDataType.STRING, newId.toString())
        return newId
    }

    private fun findEntityByUUID(entities: List<Entity>, uuid: UUID): Entity? {
        for (entity in entities) {
            val stored = entity.persistentDataContainer.get(entityUuidKey, PersistentDataType.STRING)
            if (stored != null && stored == uuid.toString()) return entity
        }
        return null
    }

    private fun spawnEntityCopy(source: Entity, world: World, uuid: UUID): Entity {
        // Clone the original location but move it into the target world
        val originalLoc = source.location.clone().apply { this.world = world }

        // Adjust the spawn location if the original spot is unsafe
        val safeLoc = findSafeSpawn(originalLoc)

        // Spawn the entity at the safe location
        val spawned = world.spawnEntity(safeLoc, source.type)

        // Preserve the persistent UUID tag
        spawned.persistentDataContainer.set(
            entityUuidKey,
            PersistentDataType.STRING,
            uuid.toString()
        )

        return spawned
    }

    private fun findSafeSpawn(location: Location): Location {
        var loc = location.clone()

        // If the spawn block is solid, move up until safe
        while (loc.block.type.isSolid) {
            loc = loc.add(0.0, 1.0, 0.0)
        }

        // If the block above is solid, move down until safe
        while (loc.clone().add(0.0, 1.0, 0.0).block.type.isSolid) {
            loc = loc.add(0.0, -1.0, 0.0)
        }

        return loc
    }

    private fun copyEntityData(source: Entity, target: Entity) {
        setNameCompat(target, getNameCompat(source))
        target.isCustomNameVisible = source.isCustomNameVisible

        if (source is LivingEntity && target is LivingEntity) {
            val srcEq = source.equipment
            val dstEq = target.equipment

            for (slot in EquipmentSlot.entries) {
                dstEq?.setItem(slot, srcEq?.getItem(slot)?.clone())
            }

            val max = target.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: target.health
            target.health = source.health.coerceAtMost(max)
        }

        if (source is Villager && target is Villager) {
            target.profession = source.profession
            target.villagerLevel = source.villagerLevel
            target.villagerExperience = source.villagerExperience
        }

        if (source is Cat && target is Cat) {
            target.catType = source.catType
            target.collarColor = source.collarColor
            target.isTamed = false
        }

        if (source is IronGolem && target is IronGolem) {
            target.isPlayerCreated = source.isPlayerCreated
        }

        // --- Persistence flags (vanilla-accurate!) ---
        if (source is Mob && target is Mob) {
            // Copy EXACT vanilla persistence behavior from the template world
            target.removeWhenFarAway = source.removeWhenFarAway
            target.isPersistent = source.isPersistent
        }
    }

    @Suppress("DEPRECATION")
    private fun getNameCompat(entity: Entity): Component? {
        return try {
            entity.customName() // Paper
        } catch (ex: NoSuchMethodError) {
            entity.getCustomName()?.let { Component.text(it) } // Bukkit/ArcLight
        }
    }

    @Suppress("DEPRECATION")
    private fun setNameCompat(entity: Entity, name: Component?) {
        try {
            entity.customName(name) // Paper
        } catch (ex: NoSuchMethodError) {
            entity.setCustomName(name?.toString()) // Bukkit/ArcLight fallback
        }
    }

    private fun isLeashedCompat(entity: Mob): Boolean {
        return try {
            entity.isLeashed && entity.leashHolder != null
        } catch (ex: IllegalStateException) {
            // ArcLight throws if not leashed
            false
        }
    }
}
