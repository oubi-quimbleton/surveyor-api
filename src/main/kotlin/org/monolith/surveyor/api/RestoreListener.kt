package org.monolith.surveyor.api

import org.bukkit.Chunk

fun interface RestoreListener {
    fun onChunkRestored(chunk: Chunk, durationMs: Long)
}