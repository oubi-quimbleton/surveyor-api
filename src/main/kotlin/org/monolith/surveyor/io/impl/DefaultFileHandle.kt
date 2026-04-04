package org.monolith.surveyor.io.impl

import org.monolith.surveyor.io.FileHandle
import java.nio.file.Files
import java.nio.file.Path

/**
 * Default FileHandle implementation backed by a java.nio.file.Path.
 */
internal class DefaultFileHandle(
    override val path: Path
) : FileHandle {

    override fun exists(): Boolean =
        Files.exists(path)

    override fun delete() {
        if (exists()) {
            Files.delete(path)
        }
    }

    override fun size(): Long =
        if (exists()) Files.size(path) else 0L

    override fun toString(): String = "FileHandle($path)"
}
