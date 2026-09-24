/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * ExplicitLexiconTest.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.data.rpc.censor

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExplicitLexiconTest {

    private val lexicon = ExplicitLexicon.parse(
        ratedTerms = sequenceOf("# comment", "", "beaver\t0", "addict\t1", "asshat\t2", "Arse Bandit\t2", "broken"),
        exclusions = sequenceOf("# comment", "addict", "Honky Tonk"),
        stems = sequenceOf("fuck\tanywhere", "wank\tprefix"),
    )

    @Test
    fun `includes terms rated maybe or likely profane`() {
        assertEquals(setOf("addict", "asshat"), lexicon.terms)
    }

    @Test
    fun `splits multi-word entries into phrases`() {
        assertEquals(setOf(listOf("arse", "bandit")), lexicon.phrases)
        assertEquals(setOf(listOf("honky", "tonk")), lexicon.exclusionPhrases)
        assertEquals(2, lexicon.longestPhrase)
    }

    @Test
    fun `parses stems and their matching mode`() {
        assertEquals(
            listOf(ExplicitLexicon.Stem("fuck", false), ExplicitLexicon.Stem("wank", true)),
            lexicon.stems,
        )
    }

    @Test
    fun `detector uses the supplied lexicon`() {
        val detector = ExplicitLanguageDetector(lexicon)
        assertTrue(detector.detect("you asshat").isNotEmpty())
        assertFalse(detector.detect("addict").isNotEmpty())
        assertFalse(detector.detect("beaver").isNotEmpty())
    }

    @Test
    fun `bundled lexicon loads from resources`() {
        val bundled = ExplicitLexicon.bundled()
        assertTrue(bundled.terms.size > 1000)
        assertTrue("fuck" in bundled.terms)
        assertTrue(bundled.phrases.isNotEmpty())
        assertTrue(bundled.stems.isNotEmpty())
        assertTrue("scunthorpe" in bundled.exclusions)
    }
}
