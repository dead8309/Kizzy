package com.my.kizzy.ui.screen.nintendo

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class Games(
    val image: String,
    var title: String
)
val blacklisted = listOf("\"","#",".","'","-")

fun getGamesData(context: Context): List<Games>{
    val jsonData = context.assets.open("games.json").bufferedReader().use { it.readText() }
    val gameTypeToken = object : TypeToken<List<Games>>() {}.type
    val list: List<Games> = Gson().fromJson(jsonData, gameTypeToken)
    /*list.forEach{
       it.title = it.title.replace(blacklisted)
    }*/
    return list.sortedBy { it.title }
}

private fun String.replace(skip: List<String>): String {
    val result = this.trim()
    skip.forEach{
        if (result.startsWith(it)){
            result.replace(it,"")
        }
    }
    return result
}
