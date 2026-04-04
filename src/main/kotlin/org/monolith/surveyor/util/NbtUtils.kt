package org.monolith.surveyor.util

import net.kyori.adventure.nbt.*

/**
 * Safe extraction helpers for Kyori NBT.
 */
object NbtUtils {

    fun getInt(tag: CompoundBinaryTag, key: String): Int? =
        (tag.get(key) as? IntBinaryTag)?.value()

    fun getString(tag: CompoundBinaryTag, key: String): String? =
        (tag.get(key) as? StringBinaryTag)?.value()

    fun getCompound(tag: CompoundBinaryTag, key: String): CompoundBinaryTag? =
        tag.get(key) as? CompoundBinaryTag

    fun getList(tag: CompoundBinaryTag, key: String): ListBinaryTag? =
        tag.get(key) as? ListBinaryTag
}
