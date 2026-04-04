package org.monolith.surveyor.util

import java.nio.file.Files
import java.nio.file.Path

/**
 * Safe filesystem helpers.
 */
object FileUtils {

    /**
     * Ensures a directory exists, creating it if necessary.
     */
    fun ensureDirectory(path: Path): Path {
        if (!Files.exists(path)) {
            Files.createDirectories(path)
        }
        return path
    }

    /**
     * Deletes a directory recursively.
     */
    fun deleteRecursively(path: Path) {
        if (!Files.exists(path)) return

        Files.walk(path)
            .sorted(Comparator.reverseOrder())
            .forEach { Files.deleteIfExists(it) }
    }
}
