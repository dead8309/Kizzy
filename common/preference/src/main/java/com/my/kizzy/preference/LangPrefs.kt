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
private const val TRADITIONAL_CHINESE = 9
private const val BURMESE = 10
private const val VIETNAMESE = 11
private const val ITALIAN = 12
private const val FILIPINO = 13
private const val FRENCH = 14
private const val CROATIAN = 15
private const val FARSI = 16
private const val GERMAN = 17
private const val THAI = 18
private const val JAPANESE = 19
private const val KOREAN = 20
private const val ARABIC = 21
private const val SPANISH = 22
private const val HEBREW = 23

val languages: Map<Int, String> =
    mapOf(
        Pair(ENGLISH, "en"),
        Pair(TURKISH, "tr"),
        Pair(DUTCH, "nl"),
        Pair(RUSSIAN, "ru"),
        Pair(POLISH, "pl"),
        Pair(PORTUGUESE, "pt"),
        Pair(INDONESIAN, "in"),
        Pair(SIMPLIFIED_CHINESE, "zh"),
        Pair(TRADITIONAL_CHINESE, "zh-rTW"),
        Pair(BURMESE, "mm"),
        Pair(VIETNAMESE, "vi"),
        Pair(ITALIAN, "it"),
        Pair(FILIPINO, "fil"),
        Pair(FRENCH, "fr"),
        Pair(CROATIAN, "hr"),
        Pair(FARSI, "fa"),
        Pair(GERMAN, "de"),
        Pair(THAI, "th"),
        Pair(JAPANESE, "ja"),
        Pair(KOREAN, "kr"),
        Pair(ARABIC, "ar"),
        Pair(SPANISH, "es"),
        Pair(HEBREW, "he")
    ).toList().sortedBy { (_, value) -> value }.toMap()

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
            TRADITIONAL_CHINESE -> R.string.locale_zh_rtw
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
            CROATIAN -> R.string.locale_hr
            FARSI -> R.string.locale_fa
            GERMAN -> R.string.locale_de
            THAI -> R.string.locale_th
            JAPANESE -> R.string.locale_ja
            KOREAN -> R.string.locale_kr
            ARABIC -> R.string.locale_ar
            SPANISH -> R.string.locale_es
            HEBREW -> R.string.locale_he
            else -> R.string.follow_system
        }
    )
}