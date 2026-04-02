package org.monolith.surveyor.io

/**
 * Coordinates access to files to prevent concurrent writes and corruption.
 *
 * This is intentionally generic; higher layers decide how to use it.
 */
interface FileLockManager {

    /**
     * Executes [action] while holding a shared/read lock for the given file.
     */
    fun <T> withReadLock(file: FileHandle, action: () -> T): T

    /**
     * Executes [action] while holding an exclusive/write lock for the given file.
     */
    fun <T> withWriteLock(file: FileHandle, action: () -> T): T
}
