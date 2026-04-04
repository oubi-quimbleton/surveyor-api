package org.monolith.surveyor.io.impl

import org.monolith.surveyor.io.FileHandle
import org.monolith.surveyor.io.FileLockManager
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Default in-process file lock manager using JVM locks.
 *
 * This prevents concurrent access within the same JVM. It does not provide
 * cross-process locking.
 */
internal class DefaultFileLockManager : FileLockManager {

    private val locks = ConcurrentHashMap<String, ReentrantLock>()

    private fun lockFor(file: FileHandle): ReentrantLock =
        locks.computeIfAbsent(file.path.toAbsolutePath().normalize().toString()) {
            ReentrantLock()
        }

    override fun <T> withReadLock(file: FileHandle, action: () -> T): T {
        // For now, read and write share the same exclusive lock.
        val lock = lockFor(file)
        return lock.withLock(action)
    }

    override fun <T> withWriteLock(file: FileHandle, action: () -> T): T {
        val lock = lockFor(file)
        return lock.withLock(action)
    }
}
