/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * ExplicitTextMasker.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.data.rpc.censor

object ExplicitTextMasker {
    private const val MASK = "*"

    /**
     * Masks every range in [ranges] (non-overlapping, ascending), keeping the first and last
     * character of each and replacing everything in between with `*`, e.g. "Fucked" -> "F****d".
     * Two-character ranges keep only the first character and single characters are fully masked.
     */
    fun mask(text: String, ranges: List<IntRange>): String {
        if (ranges.isEmpty()) return text
        val result = StringBuilder(text.length)
        var cursor = 0
        for (range in ranges) {
            result.append(text, cursor, range.first)
            result.append(maskWord(text.substring(range.first, range.last + 1)))
            cursor = range.last + 1
        }
        result.append(text, cursor, text.length)
        return result.toString()
    }

    private fun maskWord(word: String): String {
        val codePoints = word.codePoints().toArray()
        val first = String(Character.toChars(codePoints.first()))
        return when (codePoints.size) {
            1 -> MASK
            2 -> first + MASK
            else -> first + MASK.repeat(codePoints.size - 2) + String(Character.toChars(codePoints.last()))
        }
    }
}
