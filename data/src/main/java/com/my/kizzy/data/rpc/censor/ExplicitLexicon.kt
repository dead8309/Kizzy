/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * ExplicitLexicon.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.data.rpc.censor

import java.util.Locale

/**
 * Word data used by [ExplicitLanguageDetector]. All entries are lowercase.
 *
 * @property terms single explicit words
 * @property phrases explicit multi-word phrases, each split into words
 * @property exclusions words and phrases that must never be treated as explicit
 * @property stems profane stems that are also explicit inside longer words
 */
class ExplicitLexicon(
    val terms: Set<String>,
    val phrases: Set<List<String>>,
    val exclusions: Set<String>,
    val exclusionPhrases: Set<List<String>>,
    val stems: List<Stem>,
) {
    data class Stem(val value: String, val prefixOnly: Boolean)

    val longestPhrase: Int = maxOf(
        phrases.maxOfOrNull { it.size } ?: 1,
        exclusionPhrases.maxOfOrNull { it.size } ?: 1,
    )

    companion object {
        /** cuss rates each entry 0 (unlikely profane), 1 (maybe) or 2 (likely). */
        private const val MIN_RATING = 1
        private const val RESOURCE_DIR = "/com/my/kizzy/data/rpc/censor"
        private val WORD_SEPARATOR = Regex("[\\s-]+")

        /** Loads the lexicon bundled with the app. Performs I/O, so cache the result. */
        fun bundled(): ExplicitLexicon = parse(
            ratedTerms = readResource("cuss-en.tsv"),
            exclusions = readResource("exclusions.txt"),
            stems = readResource("stems.txt"),
        )

        fun parse(
            ratedTerms: Sequence<String>,
            exclusions: Sequence<String>,
            stems: Sequence<String>,
        ): ExplicitLexicon {
            val terms = ratedTerms.contentLines().mapNotNull { line ->
                val (term, rating) = line.split('\t').takeIf { it.size == 2 } ?: return@mapNotNull null
                term.takeIf { (rating.trim().toIntOrNull() ?: 0) >= MIN_RATING }
            }.map(::splitWords).toList()
            val excluded = exclusions.contentLines().map(::splitWords).toList()
            return ExplicitLexicon(
                terms = terms.filter { it.size == 1 }.map { it.single() }.toSet(),
                phrases = terms.filter { it.size > 1 }.toSet(),
                exclusions = excluded.filter { it.size == 1 }.map { it.single() }.toSet(),
                exclusionPhrases = excluded.filter { it.size > 1 }.toSet(),
                stems = stems.contentLines().map { line ->
                    val parts = line.split('\t')
                    Stem(parts[0].lowercase(Locale.ROOT), parts.getOrNull(1)?.trim() == "prefix")
                }.toList(),
            )
        }

        private fun Sequence<String>.contentLines() =
            map { it.trim() }.filter { it.isNotEmpty() && !it.startsWith("#") }

        private fun splitWords(entry: String): List<String> =
            entry.lowercase(Locale.ROOT).split(WORD_SEPARATOR).filter { it.isNotEmpty() }

        private fun readResource(name: String): Sequence<String> {
            val stream = ExplicitLexicon::class.java.getResourceAsStream("$RESOURCE_DIR/$name")
                ?: error("Missing explicit language resource: $name")
            return stream.bufferedReader().use { it.readLines() }.asSequence()
        }
    }
}
