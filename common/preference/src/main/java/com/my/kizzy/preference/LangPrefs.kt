/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * LangPrefs.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.preference

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.core.os.LocaleListCompat
import com.my.kizzy.resources.R

// Languages Index number
const val SYSTEM_DEFAULT = 0
private const val ENGLISH = 1
private const val TURKISH = 2
private const val DUTCH = 3
private const val RUSSIAN = 4
private const val POLISH = 5
private const val PORTUGUESE = 6
private const val INDONESIAN = 7
private const val SIMPLIFIED_CHINESE = 8
private const val BURMESE = 9
private const val VIETNAMESE = 10
private const val ITALIAN = 11
private const val FILIPINO = 12
private const val FRENCH = 13

val languages: Map<Int, String> =
    mapOf(
        Pair(ENGLISH, "en"),
        Pair(TURKISH, "tr"),
        Pair(DUTCH,"nl"),
        Pair(RUSSIAN, "ru"),
        Pair(POLISH, "pl"),
        Pair(PORTUGUESE, "pt"),
        Pair(INDONESIAN, "in"),
        Pair(SIMPLIFIED_CHINESE, "zh"),
        Pair(BURMESE, "mm"),
        Pair(VIETNAMESE, "vi"),
        Pair(ITALIAN, "it"),
        Pair(FILIPINO, "fil"),
        Pair(FRENCH, "fr")
    )

fun getLanguageConfig(languageNumber: Int = Prefs[Prefs.LANGUAGE]): String {
    return if (languages.containsKey(languageNumber)) languages[languageNumber].toString() else ""
}

private fun getLanguageNumberByCode(languageCode: String): Int {
    languages.entries.forEach {
        if (it.value == languageCode) return it.key
    }
    return SYSTEM_DEFAULT
}

fun getLanguageNumber(): Int {
    return if (Build.VERSION.SDK_INT >= 33)
        getLanguageNumberByCode(
            LocaleListCompat.getAdjustedDefault()[0]?.toLanguageTag().toString()
        )
    else Prefs[Prefs.LANGUAGE, SYSTEM_DEFAULT]
}

@Composable
fun getLanguageDesc(language: Int = getLanguageNumber()): String {
    return stringResource(
        when (language) {
            SIMPLIFIED_CHINESE -> R.string.locale_zh
            ENGLISH -> R.string.locale_en
            TURKISH -> R.string.locale_tr
            RUSSIAN -> R.string.locale_ru
            INDONESIAN -> R.string.locale_in
            DUTCH -> R.string.locale_nl
            POLISH -> R.string.locale_pl
            PORTUGUESE -> R.string.locale_pt
            BURMESE -> R.string.locale_mm
            VIETNAMESE -> R.string.locale_vi
            ITALIAN -> R.string.locale_it
            FILIPINO -> R.string.locale_fil
            FRENCH -> R.string.locale_fr
            else -> R.string.follow_system
        }
    )
}