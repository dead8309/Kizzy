package com.my.kizzy.utils

import android.content.Context
import android.content.res.AssetManager
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken

data class GameItem(
    @SerializedName("label")
    val label: String,
    @SerializedName("link")
    val link: String,
    @SerializedName("title")
    val title: String
)

data class Xbox(
    @SerializedName("titlebackground")
    val titlebackground: String,
    @SerializedName("titleicon")
    val titleicon: String,
    @SerializedName("titleimage")
    val titleimage: String,
    @SerializedName("titlename")
    val titlename: String,
    @SerializedName("type")
    val type: Int
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
    fun getXboxData(): List<Xbox>{
        val jsonData = assetManager.open("xbox.json").bufferedReader().use { it.readText() }
        val gameTypeToken = object : TypeToken<List<Xbox>>() {}.type
        val list: List<Xbox> = Gson().fromJson(jsonData, gameTypeToken)
        return list.sortedBy { it.titlename }
    }
}