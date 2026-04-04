package org.monolith.surveyor.nbt.impl

import net.kyori.adventure.nbt.BinaryTagIO
import net.kyori.adventure.nbt.CompoundBinaryTag
import org.monolith.surveyor.io.FileHandle
import org.monolith.surveyor.io.FileLockManager
import org.monolith.surveyor.nbt.RegionReader
import java.io.ByteArrayInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import java.nio.file.StandardOpenOption
import java.util.zip.GZIPInputStream
import java.util.zip.InflaterInputStream

/**
 * Default RegionReader implementation for standard Anvil region files.
 */
internal class DefaultRegionReader(
    private val lockManager: FileLockManager
) : RegionReader {

    override fun readChunk(regionFile: FileHandle, localChunkX: Int, localChunkZ: Int): CompoundBinaryTag? {
        if (!regionFile.exists()) return null
        require(localChunkX in 0..31 && localChunkZ in 0..31) {
            "Local chunk coordinates must be in [0,31], got ($localChunkX, $localChunkZ)"
        }

        return lockManager.withReadLock(regionFile) {
            FileChannel.open(regionFile.path, StandardOpenOption.READ).use { channel ->
                val header = ByteBuffer.allocate(4096).order(ByteOrder.BIG_ENDIAN)
                channel.read(header, 0)
                header.flip()

                val index = localChunkX + localChunkZ * 32
                val offsetEntry = header.intAt(index)

                if (offsetEntry == 0) {
                    return@withReadLock null
                }

                val sectorOffset = (offsetEntry ushr 8) and 0xFFFFFF
                val sectorCount = offsetEntry and 0xFF

                if (sectorOffset == 0 || sectorCount == 0) {
                    return@withReadLock null
                }

                val chunkHeader = ByteBuffer.allocate(5).order(ByteOrder.BIG_ENDIAN)
                val chunkStart = sectorOffset.toLong() * 4096L
                channel.read(chunkHeader, chunkStart)
                chunkHeader.flip()

                val length = chunkHeader.int
                if (length <= 0 || length > sectorCount * 4096 - 5) {
                    return@withReadLock null
                }

                val compressionType = chunkHeader.get().toInt() and 0xFF
                val data = ByteArray(length - 1)
                val dataBuffer = ByteBuffer.wrap(data)
                channel.read(dataBuffer, chunkStart + 5)

                val decompressedStream = when (compressionType) {
                    1 -> GZIPInputStream(ByteArrayInputStream(data))
                    2 -> InflaterInputStream(ByteArrayInputStream(data))
                    3 -> ByteArrayInputStream(data)
                    else -> throw IllegalStateException("Unsupported compression type: $compressionType")
                }

                decompressedStream.use { input ->
                    BinaryTagIO.reader().read(input) as CompoundBinaryTag
                }
            }
        }
    }

    private fun ByteBuffer.intAt(index: Int): Int {
        val pos = index * 4
        return get(pos).toInt().shl(24) or
                (get(pos + 1).toInt() and 0xFF shl(16)) or
                (get(pos + 2).toInt() and 0xFF shl(8)) or
                (get(pos + 3).toInt() and 0xFF)
    }
}
