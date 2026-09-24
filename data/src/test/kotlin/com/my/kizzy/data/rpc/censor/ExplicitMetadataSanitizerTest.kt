/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * ExplicitMetadataSanitizerTest.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.data.rpc.censor

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class ExplicitMetadataSanitizerTest(
    private val input: String,
    private val expected: String,
) {
    @Test
    fun sanitize() {
        assertEquals(expected, sanitizer.sanitize(input))
    }

    companion object {
        private val sanitizer = ExplicitMetadataSanitizer()

        @JvmStatic
        @Parameterized.Parameters(name = "{0} -> {1}")
        fun cases() = listOf(
            // Casing is detected case-insensitively and preserved in the output
            "fuck" to "f**k",
            "Fuck" to "F**k",
            "FUCK" to "F**K",
            "fUcK" to "f**K",
            // Grammatical variants and plurals
            "fucked" to "f****d",
            "FUCKED" to "F****D",
            "fucking" to "f*****g",
            "fucker" to "f****r",
            "fuckers" to "f*****s",
            "bitch" to "b***h",
            "bitches" to "b*****s",
            "shit" to "s**t",
            "shitty" to "s****y",
            "cunts" to "c***s",
            "whores" to "w****s",
            "wankers" to "w*****s",
            "dicks" to "d***s",
            // Compounds
            "asshole" to "a*****e",
            "bullshit" to "b******t",
            "motherfucker" to "m**********r",
            "Motherfuckers" to "M***********s",
            "clusterfuck" to "c*********k",
            "Shitstorm" to "S*******m",
            // Full titles
            "Fucked on Sight" to "F****d on Sight",
            "Fuck This Shit" to "F**k This S**t",
            "What the FUCK?" to "What the F**K?",
            "Bitch, Don't Kill My Vibe" to "B***h, Don't Kill My Vibe",
            "Shit, Fuck & Damn" to "S**t, F**k & D**n",
            // Punctuation, brackets, quotes, apostrophes and hyphens
            "(Fuck)" to "(F**k)",
            "[Bitch]" to "[B***h]",
            "\"Shit\"" to "\"S**t\"",
            "\u201CFuck\u201D" to "\u201CF**k\u201D",
            "...fuck!!!" to "...f**k!!!",
            "Fuck's Sake" to "F**k's Sake",
            "Shit\u2019ll Happen" to "S**t\u2019ll Happen",
            "Fuckin' Perfect" to "F****n' Perfect",
            "Mother-Fucker" to "Mother-F****r",
            "Shit-Faced" to "S**t-Faced",
            "Title (feat. Bitch)" to "Title (feat. B***h)",
            "Song - Fuck You (Remix)" to "Song - F**k You (Remix)",
            // Common evasion spellings
            "Sh1t" to "S**t",
            "b!tch" to "b***h",
            "FUUUUCK" to "F*****K",
            "@sshole" to "@*****e",
            // Multi-word phrases from the dataset
            "Curry Muncher" to "C***y M*****r",
            "Muncher" to "Muncher",
            "Cock" to "C**k",
            "Cock a Doodle Doo" to "Cock a Doodle Doo",
            "Cock-a-Doodle-Doo" to "Cock-a-Doodle-Doo",
            "Pussy Cat" to "Pussy Cat",
            "Doo Wop (That Thing)" to "Doo Wop (That Thing)",
            // Unicode, emoji and non-English metadata
            "\uD83D\uDD25 Fuck \uD83D\uDD25" to "\uD83D\uDD25 F**k \uD83D\uDD25",
            "Fuck ça" to "F**k ça",
            "東京フラッシュ" to "東京フラッシュ",
            "Motörhead - Ace of Spades" to "Motörhead - Ace of Spades",
            "Café Tacvba" to "Café Tacvba",
            "Шит 🎧 Fuck" to "Шит 🎧 F**k",
            // Empty and whitespace-only values
            "" to "",
            "   " to "   ",
        ).map { arrayOf(it.first, it.second) }
    }
}
