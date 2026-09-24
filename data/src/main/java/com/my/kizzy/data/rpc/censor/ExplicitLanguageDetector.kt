/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * ExplicitLanguageDetector.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.data.rpc.censor

import java.util.Locale

/**
 * Finds explicit words in free text using an [ExplicitLexicon].
 *
 * Text is split into whole words, so innocent words that merely contain a
 * profane sequence are not matched. A word is explicit when it, or a simple
 * inflection of it (plural, past tense, -ing, -er, -y), is in the lexicon,
 * or when it contains one of the lexicon's compound stems. Common evasions
 * such as leetspeak ("sh1t", "@ss") and stretched letters ("fuuuck") are
 * normalised before lookup.
 *
 * Instances are immutable and safe to share between threads.
 */
class ExplicitLanguageDetector(private val lexicon: ExplicitLexicon) {

    /**
     * Returns the character ranges of [text] that contain explicit words,
     * in ascending order. Trailing contractions ("'s", "'ll") are not included.
     */
    fun detect(text: String): List<IntRange> {
        val words = tokenize(text)
        if (words.isEmpty()) return emptyList()

        val lowercase = words.map { text.substring(it.first, it.last + 1).lowercase(Locale.ROOT) }
        val protected = BooleanArray(words.size)
        val explicit = BooleanArray(words.size)

        markPhrases(text, words, lowercase, lexicon.exclusionPhrases, protected)
        markPhrases(text, words, lowercase, lexicon.phrases, explicit, skip = protected)

        return words.indices
            .filter { explicit[it] || (!protected[it] && isExplicitWord(lowercase[it])) }
            .map { words[it] }
    }

    private fun markPhrases(
        text: String,
        words: List<IntRange>,
        lowercase: List<String>,
        phrases: Set<List<String>>,
        marks: BooleanArray,
        skip: BooleanArray? = null,
    ) {
        if (phrases.isEmpty()) return
        for (start in words.indices) {
            for (size in minOf(lexicon.longestPhrase, words.size - start) downTo 2) {
                val end = start + size
                if (skip != null && (start until end).any { skip[it] }) continue
                if (!isContiguous(text, words, start, end)) continue
                if (lowercase.subList(start, end) in phrases) {
                    (start until end).forEach { marks[it] = true }
                    break
                }
            }
        }
    }

    private fun isContiguous(text: String, words: List<IntRange>, start: Int, end: Int): Boolean =
        (start + 1 until end).all { i ->
            val gap = text.substring(words[i - 1].last + 1, words[i].first)
            gap.isNotEmpty() && gap.all { it.isWhitespace() || it == '-' }
        }

    private fun isExplicitWord(word: String): Boolean {
        if (word in lexicon.exclusions) return false
        return candidateForms(word).any(::isExplicitForm)
    }

    private fun isExplicitForm(form: String): Boolean {
        if (form in lexicon.exclusions) return false
        if (form in lexicon.terms) return true

        val roots = inflectionRoots(form)
        if (roots.any { it in lexicon.exclusions }) return false
        if (roots.any { it in lexicon.terms }) return true

        return lexicon.stems.any { stem ->
            if (stem.prefixOnly) form.startsWith(stem.value) else form.contains(stem.value)
        }
    }

    private fun candidateForms(word: String): Set<String> {
        val plain = word.filterNot { it in APOSTROPHES }
        val unLeeted = plain.map { LEET[it] ?: it }.joinToString("")
        return buildSet {
            for (form in listOf(plain, unLeeted)) {
                add(form)
                add(collapseRuns(form, keep = 1))
                add(collapseRuns(form, keep = 2))
            }
        }
    }

    /** Shortens runs of three or more identical characters to [keep] characters. */
    private fun collapseRuns(value: String, keep: Int): String {
        val result = StringBuilder(value.length)
        var start = 0
        while (start < value.length) {
            var end = start
            while (end < value.length && value[end] == value[start]) end++
            val runLength = end - start
            repeat(if (runLength >= 3) keep else runLength) { result.append(value[start]) }
            start = end
        }
        return result.toString()
    }

    private fun inflectionRoots(form: String): List<String> {
        val roots = ArrayList<String>(4)
        for (suffix in SUFFIXES) {
            if (!form.endsWith(suffix)) continue
            val root = form.dropLast(suffix.length)
            if (root.length < 3) continue
            when (suffix) {
                "s" -> roots += root
                "es" -> if (SIBILANT_ENDINGS.any { root.endsWith(it) }) roots += root
                "ies", "ier", "iest" -> roots += root + "y"
                else -> {
                    if (root.length >= 4) roots += root
                    if (root.length >= 5 && root[root.lastIndex] == root[root.lastIndex - 1]) {
                        roots += root.dropLast(1)
                    }
                    roots += root + "e"
                }
            }
        }
        return roots
    }

    private fun tokenize(text: String): List<IntRange> {
        val words = mutableListOf<IntRange>()
        var i = 0
        while (i < text.length) {
            val codePoint = text.codePointAt(i)
            if (!isWordChar(codePoint) && !(codePoint.isPrefixSymbol() && isLetterAt(text, i + 1))) {
                i += Character.charCount(codePoint)
                continue
            }
            val start = i
            i += Character.charCount(codePoint)
            while (i < text.length) {
                val next = text.codePointAt(i)
                when {
                    isWordChar(next) -> i += Character.charCount(next)
                    next.toChar() in CONNECTORS && i + 1 < text.length &&
                        isWordChar(text.codePointAt(i + 1)) -> i++

                    else -> break
                }
            }
            val end = CONTRACTION.find(text.substring(start, i))?.let { start + it.range.first } ?: i
            words += start until end
        }
        return words
    }

    private fun isWordChar(codePoint: Int): Boolean =
        Character.isLetterOrDigit(codePoint) || Character.getType(codePoint).toByte() in MARKS

    private fun isLetterAt(text: String, index: Int): Boolean =
        index < text.length && Character.isLetter(text.codePointAt(index))

    private fun Int.isPrefixSymbol() = this == '@'.code || this == '$'.code

    private companion object {
        val APOSTROPHES = setOf('\'', '\u2019')
        val CONNECTORS = APOSTROPHES + setOf('@', '$', '!')
        val CONTRACTION = Regex("['\u2019](s|ll|re|ve|d|m)$", RegexOption.IGNORE_CASE)
        val MARKS = setOf(
            Character.NON_SPACING_MARK,
            Character.COMBINING_SPACING_MARK,
            Character.ENCLOSING_MARK,
        )
        val LEET = mapOf(
            '0' to 'o', '1' to 'i', '3' to 'e', '4' to 'a', '5' to 's', '7' to 't',
            '@' to 'a', '$' to 's', '!' to 'i',
        )
        val SUFFIXES = listOf("ings", "ing", "iest", "ier", "ies", "ers", "er", "ed", "es", "in", "s", "y")
        val SIBILANT_ENDINGS = listOf("s", "x", "z", "sh", "ch")
    }
}
