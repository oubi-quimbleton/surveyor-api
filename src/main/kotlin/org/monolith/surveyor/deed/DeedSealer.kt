package org.monolith.surveyor.deed

import org.bukkit.GameRule
import org.bukkit.World

internal class DeedSealer {

    fun seal(world: World) {
        disableAutosave(world)
        applyGameRules(world)
        resetWeather(world)
    }

    private fun disableAutosave(world: World) {
        world.isAutoSave = false
    }

    private fun applyGameRules(world: World) {
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false)
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false)
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false)
        world.setGameRule(GameRule.DO_FIRE_TICK, false)
        world.setGameRule(GameRule.DISABLE_RAIDS, true)
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false)
        world.setGameRule(GameRule.RANDOM_TICK_SPEED, 0)
        world.setGameRule(GameRule.DO_TILE_DROPS, false)
        world.setGameRule(GameRule.DO_ENTITY_DROPS, false)
        world.setGameRule(GameRule.DO_MOB_LOOT, false)
        world.setGameRule(GameRule.DISABLE_ELYTRA_MOVEMENT_CHECK, false)

    }

    private fun resetWeather(world: World) {
        world.setStorm(false)
        world.isThundering = false
    }
}
