package org.monolith.surveyor.legacy.api

import org.bukkit.Chunk

fun interface RestoreListener {
    fun onChunkRestored(chunk: Chunk, durationMs: Long)
}