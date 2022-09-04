package com.my.kizzy.ui.screen.settings.language

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.my.kizzy.ui.common.BackButton
import com.my.kizzy.ui.common.PreferenceSingleChoiceItem
import com.my.kizzy.utils.Prefs
import com.my.kizzy.utils.Prefs.LANGUAGE


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Language(navController: NavController) {
    var locale by remember {
        mutableStateOf(
            Prefs[LANGUAGE, "English"]
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
    ) {
        LazyColumn(modifier = Modifier.padding(it)) {
            for (language in languages()) {
                item {
                    PreferenceSingleChoiceItem(
                        text = language.key + " (${language.value}",
                        selected = language.key == locale
                    ) {
                        locale = language.key
                        Prefs[LANGUAGE] = language
                    }
                }
            }
        }
    }
}

fun languages(): Map<String, String> =
    mapOf(
        Pair("English", "en"),
        Pair("Arabic", "ar"),
        Pair("Russian", "ba"),
        Pair("Spanish", "es"),
        Pair("French", "fr"),
        Pair("Japanese", "ja"),
        Pair("Korean", "ko"),
        Pair("Indonesian", "in"),
        Pair("Vietnamese", "vi"),
        Pair("Chinese", "zh"),
        Pair("Turkish", "tr"),
        Pair("Portuguese", "pt"),
        Pair("Polish", "pl"),
        Pair("Bangla", "bn"),
        Pair("Thai", "th"),
        Pair("Greek", "el"),
        Pair("Italian", "it"),
        Pair("German", "de"),
        Pair("Malay", "ms"),
        Pair("Cebuano", "ceb"),
        Pair("Romania", "ro"),
        Pair("Thailand", "th"),
        Pair("Laos", "lo"),
        Pair("Myanmar", "my"),
        Pair("Tagalog (Philippines)", "phi")
    )