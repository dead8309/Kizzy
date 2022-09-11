package com.my.kizzy.ui.screen.nintendo

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class Games(
    val image: String,
    val title: String
)

fun getGamesData(context: Context): List<Games>{
    val jsonData = context.assets.open("games.json").bufferedReader().use { it.readText() }
    val gameTypeToken = object : TypeToken<List<Games>>() {}.type
    return Gson().fromJson(jsonData, gameTypeToken)
}