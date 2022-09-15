package com.my.kizzy.ui.screen.nintendo

import android.content.Context
import android.content.res.AssetManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class GameItem(
    val label: String,
    val link: String,
    val title: String
)
object Games {
   private lateinit var assetManager: AssetManager

    fun init(context: Context) {
        assetManager = context.assets
    }

    fun getGamesData(): List<GameItem> {
        val jsonData = assetManager.open("games.json").bufferedReader().use { it.readText() }
        val gameTypeToken = object : TypeToken<List<GameItem>>() {}.type
        val list: List<GameItem> = Gson().fromJson(jsonData, gameTypeToken)

        return list.sortedBy { it.label }
    }
}