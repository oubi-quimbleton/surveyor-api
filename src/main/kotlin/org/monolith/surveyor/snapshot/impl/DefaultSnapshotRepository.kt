package org.monolith.surveyor.snapshot.impl

import net.kyori.adventure.nbt.BinaryTagIO
import net.kyori.adventure.nbt.CompoundBinaryTag
import net.kyori.adventure.nbt.IntBinaryTag
import net.kyori.adventure.nbt.StringBinaryTag
import org.monolith.surveyor.api.ChunkCoordinate
import org.monolith.surveyor.api.WorldId
import org.monolith.surveyor.snapshot.SnapshotRepository
import java.io.InputStream
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.time.Instant
import java.util.UUID

/**
 * Filesystem-based SnapshotRepository.
 *
 * Layout:
 *   snapshots/
 *     <id>/
 *       metadata.nbt
 *       chunks/
 *         x_z.nbt
 */
internal class DefaultSnapshotRepository(
    private val rootDirectory: Path
) : SnapshotRepository {

    private val snapshotsDir: Path = rootDirectory.resolve("snapshots")

    init {
        Files.createDirectories(snapshotsDir)
    }

    override fun saveSnapshot(
        worldId: WorldId,
        label: String?,
        chunks: Collection<SnapshotRepository.SnapshotChunk>
    ): String {
        val id = UUID.randomUUID().toString()
        val dir = snapshotsDir.resolve(id)
        val chunksDir = dir.resolve("chunks")

        Files.createDirectories(chunksDir)

        // write chunks
        for (chunk in chunks) {
            val file = chunksDir.resolve("${chunk.coordinate.x}_${chunk.coordinate.z}.nbt")
            Files.newOutputStream(file).use { out ->
                writeChunkTag(chunk, out)
            }
        }

        // write metadata
        val metadata = CompoundBinaryTag.builder()
            .put("id", StringBinaryTag.stringBinaryTag(id))
            .put("worldId", StringBinaryTag.stringBinaryTag(worldId.id))
            .apply {
                if (label != null) {
                    put("label", StringBinaryTag.stringBinaryTag(label))
                }
            }
            .put("createdAtEpochSecond", IntBinaryTag.intBinaryTag(Instant.now().epochSecond.toInt()))
            .put("chunkCount", IntBinaryTag.intBinaryTag(chunks.size))
            .build()

        Files.newOutputStream(dir.resolve("metadata.nbt")).use { out ->
            BinaryTagIO.writer().write(metadata, out)
        }

        return id
    }

    override fun listSnapshots(worldId: WorldId): List<SnapshotRepository.SnapshotEntry> {
        if (!Files.exists(snapshotsDir)) return emptyList()

        return Files.list(snapshotsDir).use { stream ->
            stream
                .toList()
                .filter { Files.isDirectory(it) }
                .mapNotNull { dir ->
                    val metadataFile = dir.resolve("metadata.nbt")
                    if (!Files.exists(metadataFile)) return@mapNotNull null

                    Files.newInputStream(metadataFile).use { input ->
                        val tag = BinaryTagIO.reader().read(input) as? CompoundBinaryTag ?: return@mapNotNull null
                        val entryWorldId = (tag.get("worldId") as? StringBinaryTag)?.value() ?: return@mapNotNull null
                        if (entryWorldId != worldId.id) return@mapNotNull null

                        toEntry(dir.fileName.toString(), tag)
                    }
                }
        }
    }

    override fun loadSnapshotChunks(id: String): List<SnapshotRepository.SnapshotChunk> {
        val dir = snapshotsDir.resolve(id)
        val chunksDir = dir.resolve("chunks")
        if (!Files.exists(chunksDir)) return emptyList()

        return Files.list(chunksDir).use { stream ->
            stream
                .toList() // convert Stream<Path> → List<Path>
                .filter { Files.isRegularFile(it) && it.fileName.toString().endsWith(".nbt") }
                .mapNotNull { file ->
                    Files.newInputStream(file).use { input ->
                        readChunkTag(input)
                    }
                }
        }
    }

    override fun loadSnapshotEntry(id: String): SnapshotRepository.SnapshotEntry? {
        val dir = snapshotsDir.resolve(id)
        val metadataFile = dir.resolve("metadata.nbt")
        if (!Files.exists(metadataFile)) return null

        Files.newInputStream(metadataFile).use { input ->
            val tag = BinaryTagIO.reader().read(input) as? CompoundBinaryTag ?: return null
            return toEntry(id, tag)
        }
    }

    override fun deleteSnapshot(id: String): Boolean {
        val dir = snapshotsDir.resolve(id)
        if (!Files.exists(dir)) return false

        Files.walk(dir)
            .sorted(Comparator.reverseOrder())
            .forEach { Files.deleteIfExists(it) }

        return true
    }

    private fun writeChunkTag(
        chunk: SnapshotRepository.SnapshotChunk,
        out: OutputStream
    ) {
        val tag = CompoundBinaryTag.builder()
            .put("x", IntBinaryTag.intBinaryTag(chunk.coordinate.x))
            .put("z", IntBinaryTag.intBinaryTag(chunk.coordinate.z))
            .put("chunk", chunk.nbt)
            .build()

        BinaryTagIO.writer().write(tag, out)
    }

    private fun readChunkTag(input: InputStream): SnapshotRepository.SnapshotChunk? {
        val tag = BinaryTagIO.reader().read(input) as? CompoundBinaryTag ?: return null
        val x = (tag.get("x") as? IntBinaryTag)?.value() ?: return null
        val z = (tag.get("z") as? IntBinaryTag)?.value() ?: return null
        val chunkTag = tag.get("chunk") as? CompoundBinaryTag ?: return null

        return SnapshotRepository.SnapshotChunk(
            coordinate = ChunkCoordinate(x, z),
            nbt = chunkTag
        )
    }

    private fun toEntry(id: String, tag: CompoundBinaryTag): SnapshotRepository.SnapshotEntry? {
        val worldId = (tag.get("worldId") as? StringBinaryTag)?.value() ?: return null
        val label = (tag.get("label") as? StringBinaryTag)?.value()
        val createdAtEpoch = (tag.get("createdAtEpochSecond") as? IntBinaryTag)?.value() ?: 0
        val chunkCount = (tag.get("chunkCount") as? IntBinaryTag)?.value() ?: 0

        return SnapshotRepository.SnapshotEntry(
            id = id,
            worldId = WorldId(worldId),
            label = label,
            createdAt = Instant.ofEpochSecond(createdAtEpoch.toLong()),
            chunkCount = chunkCount
        )
    }
}
