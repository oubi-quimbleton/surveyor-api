package org.monolith.surveyor.io

import java.nio.file.Path

/**
 * Simple abstraction over a filesystem path.
 *
 * This keeps the rest of the engine decoupled from java.io.File/Path details.
 */
interface FileHandle {

    /**
     * Underlying filesystem path.
     */
    val path: Path

    /**
     * Returns true if the file exists.
     */
    fun exists(): Boolean

    /**
     * Deletes the file if it exists.
     */
    fun delete()

    /**
     * Returns the file size in bytes, or 0 if it does not exist.
     */
    fun size(): Long
}
