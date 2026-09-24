/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * ExplicitLanguageDetectorTest.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.data.rpc.censor

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExplicitLanguageDetectorTest {

    private val lexicon = ExplicitLexicon.bundled()
    private val detector = ExplicitLanguageDetector(lexicon)

    @Test
    fun `detects every bundled term as a whole word`() {
        val missed = (lexicon.terms - lexicon.exclusions).filterNot { term ->
            detector.detect("Song $term (Remix)") == listOf(5 until 5 + term.length)
        }
        assertTrue("Undetected terms: $missed", missed.isEmpty())
    }

    @Test
    fun `detects every bundled phrase`() {
        val phrases = lexicon.phrases - lexicon.exclusionPhrases
        val missed = phrases.map { it.joinToString(" ") }.filterNot { phrase ->
            detector.detect(phrase).isNotEmpty()
        }
        assertTrue("Undetected phrases: $missed", missed.isEmpty())
    }

    @Test
    fun `detects explicit vocabulary across categories`() {
        val explicit = listOf(
            // Profanity and vulgarities
            "damn", "goddamn", "crap", "bastards", "bollocks", "bugger", "wtf",
            // Sexual terminology
            "blowjob", "handjobs", "cumshot", "dildos", "orgasm", "threesome", "porno", "milf",
            // Anatomical terminology
            "penis", "vagina", "clitoris", "boobs", "tits", "titties", "scrotum", "cocks",
            // Insults
            "prick", "wanker", "tosser", "skank", "twat", "dickhead", "jackass", "dumbass",
            // Scatological terminology
            "turd", "poop", "pissed", "farting",
            // Drug slang
            "reefer", "crackpipe",
            // Slurs
            "nigger", "niggas", "faggot", "fags", "kike", "spic", "chink", "retard", "tranny",
            "wetback", "raghead",
            // Grammatical variants
            "fucks", "fuckin", "shitting", "bitchy", "pussies", "sluts", "raping", "wanking",
            "pissing", "twatted",
            // Compounds
            "fuckface", "shithead", "shitfaced", "bitchass", "cocksucker", "motherfucking",
            "fuckboy", "horseshit", "dipshit", "fuckwit",
            // Evasions
            "sh1t", "b1tch", "a55hole", "fuuuuck", "shiiiit", "phuking", "biatch", "mofo",
        )
        val missed = explicit.filter { detector.detect(it).isEmpty() }
        assertTrue("Undetected explicit words: $missed", missed.isEmpty())
    }

    @Test
    fun `ignores innocent words that contain profane sequences`() {
        val innocent = listOf(
            "Scunthorpe", "class", "classic", "bass", "assassin", "passion", "compass", "Massachusetts",
            "cocktail", "cockpit", "peacock", "Hancock", "Hitchcock", "cockatoo",
            "Dickens", "Dickinson", "therapist", "grape", "scrapbook", "butter", "button", "buttress",
            "title", "titanic", "constitution", "analysis", "canal", "Uranus", "Sussex", "Essex",
            "Middlesex", "sextet", "document", "cucumber", "cumulative", "cumin", "circumstance",
            "pudding", "spicy", "spiced", "spices", "sniggering", "niggardly", "niggle",
            "Shiitake", "Mississippi", "Penistone", "hello", "shell", "Hellcat", "grasshopper",
            "Cockney", "arsenal", "arsenic", "parse", "sparse", "Swanky", "Wankel", "Sweetwater",
            "bitumen", "shuttlecock", "Cummings", "Kumquat", "Honky Tonk", "Kung Fu",
        )
        val flagged = innocent.filter { detector.detect(it).isNotEmpty() }
        assertTrue("Innocent words flagged: $flagged", flagged.isEmpty())
    }

    @Test
    fun `ignores ordinary song titles and artist names`() {
        val titles = listOf(
            "Bohemian Rhapsody", "Kung Fu Fighting", "Honky Tonk Women", "American Idiot",
            "Stupid Love", "Loser", "Trap Queen", "Demons", "Jesus Walks", "Heroin", "Cocaine",
            "Suicide Blonde", "Sweet Child O' Mine", "Ho Hey", "Spice Up Your Life", "Nude",
            "Willie Nelson", "Joe Cocker", "John Lee Hooker", "Pixies", "XTC", "The Kinks", "T.N.T.",
            "Scatman John", "Geezer Butler", "Kinky Boots", "Slave to the Rhythm", "Gypsy",
            "Sweet Transvestite", "Queer", "Sexy Back", "Oral Fixation", "Crack the Skye",
            "Mötley Crüe", "Sigur Rós - Hoppípolla", "BLACKPINK - 뚜두뚜두", "Café Tacvba", "",
            "Young Jeezy", "Three 6 Mafia", "Leroy Van Dyke", "The Hooters", "Gangsta's Paradise",
            "Honky Cat", "Redneck Woman", "Kum Ba Yah", "Gypsys, Tramps & Thieves", "The Hillbillies",
            "Puddin N' Tain",
            "Itsy Bitsy Teenie Weenie Yellow Polkadot Bikini", "Spanky and Our Gang", "Pom Poms",
            "Ooh Poo Pah Doo", "The Rinky-Dinks", "Ni Pedo", "Shatter Me", "Testing 1, 2, 3",
            "Cunning Stunts", "Muffin Man", "Spicy Pudding", "Price Tag", "Shaggy",
        )
        val flagged = titles.filter { detector.detect(it).isNotEmpty() }
        assertTrue("Innocent titles flagged: $flagged", flagged.isEmpty())
    }

    @Test
    fun `excludes trailing contractions from the detected range`() {
        assertEquals(listOf(0..3), detector.detect("Fuck's Sake"))
        assertEquals(listOf(0..5), detector.detect("Fuckin' Right"))
    }

    @Test
    fun `detects each explicit word in a title`() {
        assertEquals(listOf(0..3, 10..13, 16..20), detector.detect("Fuck This Shit, Bitch"))
    }
}
