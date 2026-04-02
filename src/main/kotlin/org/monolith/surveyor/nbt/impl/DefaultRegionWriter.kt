package org.monolith.surveyor.nbt.impl

import net.kyori.adventure.nbt.BinaryTagIO
import net.kyori.adventure.nbt.CompoundBinaryTag
import org.monolith.surveyor.io.FileHandle
import org.monolith.surveyor.io.FileLockManager
import org.monolith.surveyor.nbt.RegionWriter
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.util.zip.DeflaterOutputStream
import java.util.zip.GZIPOutputStream
import kotlin.math.ceil

/**
 * Default RegionWriter implementation for standard Anvil region files.
 *
 * Uses zlib compression by default, but supports gzip and uncompressed payloads
 * via the [compression] parameter.
 */
internal class DefaultRegionWriter(
    private val lockManager: FileLockManager,
    private val compression: Compression = Compression.ZLIB
) : RegionWriter {

    enum class Compression(val id: Int) {
        GZIP(1),
        ZLIB(2),
        UNCOMPRESSED(3)
    }

    override fun writeChunk(regionFile: FileHandle, localChunkX: Int, localChunkZ: Int, nbt: CompoundBinaryTag) {
        require(localChunkX in 0..31 && localChunkZ in 0..31) {
            "Local chunk coordinates must be in [0,31], got ($localChunkX, $localChunkZ)"
        }

        lockManager.withWriteLock(regionFile) {
            if (!regionFile.exists()) {
                Files.createDirectories(regionFile.path.parent)
                Files.createFile(regionFile.path)
                initializeEmptyHeader(regionFile)
            }

            FileChannel.open(
                regionFile.path,
                StandardOpenOption.READ,
                StandardOpenOption.WRITE
            ).use { channel ->
                val header = ByteBuffer.allocate(4096).order(ByteOrder.BIG_ENDIAN)
                channel.read(header, 0)
                header.flip()

                val offsets = IntArray(1024) { i -> header.intAt(i) }

                val fileSizeSectors = ceil(channel.size() / 4096.0).toInt().coerceAtLeast(2)
                val used = BooleanArray(fileSizeSectors) { index -> index < 2 } // header + timestamps

                for (i in offsets.indices) {
                    val entry = offsets[i]
                    if (entry != 0) {
                        val sectorOffset = (entry ushr 8) and 0xFFFFFF
                        val sectorCount = entry and 0xFF
                        if (sectorOffset > 0 && sectorCount > 0 &&
                            sectorOffset + sectorCount <= used.size
                        ) {
                            for (s in sectorOffset until sectorOffset + sectorCount) {
                                used[s] = true
                            }
                        }
                    }
                }

                val payload = encodeChunkPayload(nbt)
                val sectorsNeeded = ceil(payload.size / 4096.0).toInt().coerceAtLeast(1)

                val index = localChunkX + localChunkZ * 32
                val existingEntry = offsets[index]
                var sectorOffset = 0
                var sectorCount = 0

                if (existingEntry != 0) {
                    sectorOffset = (existingEntry ushr 8) and 0xFFFFFF
                    sectorCount = existingEntry and 0xFF
                }

                val canReuse = sectorOffset > 0 && sectorCount >= sectorsNeeded

                val targetOffset = if (canReuse) {
                    sectorOffset
                } else {
                    // find free run
                    var runStart = -1
                    var runLength = 0
                    var found = false

                    for (s in 2 until used.size) {
                        if (!used[s]) {
                            if (runStart == -1) runStart = s
                            runLength++
                            if (runLength >= sectorsNeeded) {
                                found = true
                                break
                            }
                        } else {
                            runStart = -1
                            runLength = 0
                        }
                    }

                    if (!found) {
                        runStart = used.size
                    }

                    val newSize = runStart + sectorsNeeded
                    if (newSize > used.size) {
                        channel.truncate(newSize.toLong() * 4096L)
                    }

                    for (s in runStart until runStart + sectorsNeeded) {
                        if (s < used.size) {
                            used[s] = true
                        }
                    }

                    runStart
                }

                // write payload
                val chunkStart = targetOffset.toLong() * 4096L
                val buffer = ByteBuffer.wrap(payload)
                channel.write(buffer, chunkStart)

                // update offset table
                val newEntry = (targetOffset shl 8) or sectorsNeeded
                offsets[index] = newEntry

                val headerOut = ByteBuffer.allocate(4096).order(ByteOrder.BIG_ENDIAN)
                for (i in offsets.indices) {
                    headerOut.putInt(offsets[i])
                }
                headerOut.flip()
                channel.write(headerOut, 0)
            }
        }
    }

    private fun initializeEmptyHeader(regionFile: FileHandle) {
        FileChannel.open(
            regionFile.path,
            StandardOpenOption.WRITE
        ).use { channel ->
            val header = ByteBuffer.allocate(8192) // offsets + timestamps
            channel.write(header, 0)
        }
    }

    private fun encodeChunkPayload(nbt: CompoundBinaryTag): ByteArray {
        val rawOut = ByteArrayOutputStream()
        val dataOut = DataOutputStream(rawOut)

        val compressedOut = when (compression) {
            Compression.GZIP -> GZIPOutputStream(rawOut)
            Compression.ZLIB -> DeflaterOutputStream(rawOut)
            Compression.UNCOMPRESSED -> null
        }

        if (compressedOut != null) {
            compressedOut.use { out ->
                BinaryTagIO.writer().write(nbt, out)
            }
        } else {
            BinaryTagIO.writer().write(nbt, rawOut)
        }

        val data = rawOut.toByteArray()
        val totalLength = 1 + data.size // compression byte + payload

        val full = ByteArray(4 + totalLength)
        val buf = ByteBuffer.wrap(full).order(ByteOrder.BIG_ENDIAN)
        buf.putInt(totalLength)
        buf.put(compression.id.toByte())
        buf.put(data)

        // pad to sector boundary handled by RegionWriter via sector allocation
        return full
    }

    private fun ByteBuffer.intAt(index: Int): Int {
        val pos = index * 4
        return get(pos).toInt().shl(24) or
                (get(pos + 1).toInt() and 0xFF shl(16)) or
                (get(pos + 2).toInt() and 0xFF shl(8)) or
                (get(pos + 3).toInt() and 0xFF)
    }
}
