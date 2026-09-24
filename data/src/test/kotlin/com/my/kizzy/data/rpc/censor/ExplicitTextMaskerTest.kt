/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * ExplicitTextMaskerTest.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.data.rpc.censor

import org.junit.Assert.assertEquals
import org.junit.Test

class ExplicitTextMaskerTest {

    @Test
    fun `keeps first and last character of each range`() {
        assertEquals("F**k this s**t", ExplicitTextMasker.mask("Fuck this shit", listOf(0..3, 10..13)))
    }

    @Test
    fun `leaves text outside ranges untouched`() {
        assertEquals("(\"W****r\")!", ExplicitTextMasker.mask("(\"Wanker\")!", listOf(2..7)))
    }

    @Test
    fun `returns text unchanged without ranges`() {
        assertEquals("Clean Title", ExplicitTextMasker.mask("Clean Title", emptyList()))
        assertEquals("", ExplicitTextMasker.mask("", emptyList()))
    }

    @Test
    fun `short ranges never expose the whole word`() {
        assertEquals("a *", ExplicitTextMasker.mask("a x", listOf(2..2)))
        assertEquals("W* out", ExplicitTextMasker.mask("Wn out", listOf(0..1)))
        assertEquals("a**b", ExplicitTextMasker.mask("abcb", listOf(0..3)))
    }

    @Test
    fun `masks by code point so surrogate pairs are not split`() {
        val text = "\uD835\uDC00bc\uD835\uDC01"
        assertEquals("\uD835\uDC00**\uD835\uDC01", ExplicitTextMasker.mask(text, listOf(text.indices)))
    }
}
