package org.monolith.surveyor.structure.impl

import net.kyori.adventure.nbt.CompoundBinaryTag
import net.kyori.adventure.nbt.IntArrayBinaryTag
import net.kyori.adventure.nbt.IntBinaryTag
import net.kyori.adventure.nbt.ListBinaryTag
import org.monolith.surveyor.structure.StructureRewriter

/**
 * Default StructureRewriter implementation.
 *
 * This implementation:
 * - adjusts bounding boxes in Structures/Starts
 * - adjusts piece positions where present
 *
 * It is intentionally conservative: if the layout is not recognized, it leaves data unchanged.
 */
internal class DefaultStructureRewriter : StructureRewriter {

    override fun rewrite(root: CompoundBinaryTag, blockOffsetX: Int, blockOffsetZ: Int): CompoundBinaryTag {
        if (blockOffsetX == 0 && blockOffsetZ == 0) return root

        val structures = (root.get("Structures") as? CompoundBinaryTag) ?: return root
        val starts = (structures.get("Starts") as? CompoundBinaryTag) ?: return root

        var modifiedStarts = starts
        var changed = false

        for (key in starts.keySet()) {
            val start = starts.get(key) as? CompoundBinaryTag ?: continue

            val updatedStart = rewriteStart(start, blockOffsetX, blockOffsetZ)
            if (updatedStart !== start) {
                modifiedStarts = modifiedStarts.put(key, updatedStart)
                changed = true
            }
        }

        if (!changed) return root

        val newStructures = structures.put("Starts", modifiedStarts)
        return root.put("Structures", newStructures)
    }

    private fun rewriteStart(start: CompoundBinaryTag, dx: Int, dz: Int): CompoundBinaryTag {
        var result = start
        var changed = false

        // Adjust BB: [minX, minY, minZ, maxX, maxY, maxZ]
        val bb = start.get("BB") as? IntArrayBinaryTag
        if (bb != null && bb.value().size == 6) {
            val arr = bb.value().clone()
            arr[0] += dx
            arr[2] += dz
            arr[3] += dx
            arr[5] += dz
            result = result.put("BB", IntArrayBinaryTag.intArrayBinaryTag(*arr))
            changed = true
        }

        // Adjust ChunkX/ChunkZ if present
        val chunkX = start.get("ChunkX") as? IntBinaryTag
        val chunkZ = start.get("ChunkZ") as? IntBinaryTag
        if (chunkX != null && chunkZ != null) {
            // These are chunk coordinates; convert block offset to chunk offset (floor division)
            val cdx = floorDiv(dx, 16)
            val cdz = floorDiv(dz, 16)
            if (cdx != 0 || cdz != 0) {
                result = result
                    .put("ChunkX", IntBinaryTag.intBinaryTag(chunkX.value() + cdx))
                    .put("ChunkZ", IntBinaryTag.intBinaryTag(chunkZ.value() + cdz))
                changed = true
            }
        }

        // Adjust Pieces list if present
        val pieces = start.get("Children") as? ListBinaryTag
        if (pieces != null && pieces.size() > 0 && pieces.get(0) is CompoundBinaryTag) {
            val updatedPieces = mutableListOf<CompoundBinaryTag>()
            var anyChanged = false

            for (tag in pieces) {
                val piece = tag as CompoundBinaryTag
                val updated = rewritePiece(piece, dx, dz)
                updatedPieces += updated
                if (updated !== piece) anyChanged = true
            }

            if (anyChanged) {
                result = result.put("Children", ListBinaryTag.from(updatedPieces))
                changed = true
            }
        }

        return if (changed) result else start
    }

    private fun rewritePiece(piece: CompoundBinaryTag, dx: Int, dz: Int): CompoundBinaryTag {
        var result = piece
        var changed = false

        // Adjust BB if present
        val bb = piece.get("BB") as? IntArrayBinaryTag
        if (bb != null && bb.value().size == 6) {
            val arr = bb.value().clone()
            arr[0] += dx
            arr[2] += dz
            arr[3] += dx
            arr[5] += dz
            result = result.put("BB", IntArrayBinaryTag.intArrayBinaryTag(*arr))
            changed = true
        }

        // Adjust position fields if present (common in jigsaw pieces)
        val xTag = piece.get("X") as? IntBinaryTag
        val zTag = piece.get("Z") as? IntBinaryTag
        if (xTag != null && zTag != null) {
            result = result
                .put("X", IntBinaryTag.intBinaryTag(xTag.value() + dx))
                .put("Z", IntBinaryTag.intBinaryTag(zTag.value() + dz))
            changed = true
        }

        return if (changed) result else piece
    }

    private fun floorDiv(a: Int, b: Int): Int {
        // Java-style floor division
        val r = a / b
        return if ((a xor b) < 0 && r * b != a) r - 1 else r
    }
}
