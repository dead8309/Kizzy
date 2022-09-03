package com.my.kizzy.ui.screen.settings.language

import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.my.kizzy.ui.common.BackButton
import com.my.kizzy.ui.common.PreferenceSingleChoiceItem


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Language(navController: NavController) {
    val ctx = LocalContext.current
    val sharedPreferences = ctx.getSharedPreferences("com.my.kizzy",Context.MODE_PRIVATE)
    var local by remember {
        mutableStateOf(
            getLanguageName(
            sharedPreferences
                .getString("local","")
            )
        )
    }


    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "Language",
                        style = MaterialTheme.typography.headlineLarge,
                    )
                },
                navigationIcon = {
                    BackButton {
                        navController.popBackStack()
                    }
                }
            )
        }
    ){
        LazyColumn(modifier = Modifier.padding(it)){
            for (language in languages()){
                item {
                    PreferenceSingleChoiceItem(
                        text = language,
                        selected  = language == local
                    ) {
                        local = language
                        sharedPreferences.edit().putString("local",language).apply()
                    }
                }
            }
        }
    }
}

fun getLanguageName(locale: String?): String {
    languages().forEach {
        if (it == locale)
            return it
    }
    return "English"
}



fun languages():Array<String> =
    arrayOf("English",
        "Arabic",
        "Russian",
        "Spanish",
        "French",
        "Japanese",
        "Korean",
        "Indonesian",
        "Vietnamese",
        "Chinese",
        "Turkish",
        "Portuguese",
        "Polish",
        "Bangla",
        "Thai",
        "Greek",
        "Italian",
        "German",
        "Malay",
        "Cebuano",
        "Romania",
        "Thailand",
        "Laos",
        "Myanmar",
        "Tagalog (Philippines)")