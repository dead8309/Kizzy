package com.my.kizzy.ui.screen.settings.language

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.my.kizzy.R
import com.my.kizzy.ui.common.BackButton
import com.my.kizzy.ui.common.PreferenceSingleChoiceItem
import com.my.kizzy.utils.Prefs
import com.my.kizzy.utils.Prefs.LANGUAGE
import com.yariksoffice.lingver.Lingver


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Language(onBackPressed: () -> Unit) {
    var locale by remember {
        mutableStateOf(
            Prefs[LANGUAGE, "en"]
        )
    }
    val context = LocalContext.current
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.language),
                        style = MaterialTheme.typography.headlineLarge,
                    )
                },
                navigationIcon = {
                    BackButton {
                        onBackPressed()
                    }
                }
            )
        }
    ) {
        LazyColumn(modifier = Modifier.padding(it)) {
            for (language in languages()) {
                item {
                    PreferenceSingleChoiceItem(
                        text = language.key + " (${language.value})",
                        selected = language.value == locale
                    ) {
                        locale = language.value
                        Prefs[LANGUAGE] = locale
                        Lingver.getInstance().setLocale(context,locale)
                    }
                }
            }
        }
    }
}

fun languages(): Map<String, String> =
    mapOf(
        Pair("English", "en"),
        Pair("Turkish", "tr"),
        Pair("Dutch","nl"),
        Pair("Russian", "ba"),
        Pair("Polish", "pl"),
        Pair("Portuguese", "pt"),
       /* Pair("Arabic", "ar"),
        Pair("Spanish", "es"),
        Pair("French", "fr"),
        Pair("Japanese", "ja"),
        Pair("Korean", "ko"),
        Pair("Indonesian", "in"),
        Pair("Vietnamese", "vi"),
        Pair("Chinese", "zh"),
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
        Pair("Tagalog (Philippines)", "phi")*/
    )