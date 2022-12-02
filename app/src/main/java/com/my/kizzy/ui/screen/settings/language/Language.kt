package com.my.kizzy.ui.screen.settings.language

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.my.kizzy.MainActivity
import com.my.kizzy.R
import com.my.kizzy.ui.common.BackButton
import com.my.kizzy.ui.common.PreferenceSingleChoiceItem
import com.my.kizzy.utils.Prefs
import com.my.kizzy.utils.Prefs.LANGUAGE
import com.my.kizzy.utils.Prefs.SYSTEM_DEFAULT
import com.my.kizzy.utils.Prefs.getLanguageConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Language(onBackPressed: () -> Unit) {
    var locale by remember { mutableStateOf(Prefs.getLanguageNumber()) }

    fun changeLanguage(index: Int) {
        locale = index
        Prefs[LANGUAGE] = locale
        MainActivity.setLanguage(getLanguageConfig())
    }
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
            item {
                PreferenceSingleChoiceItem(
                    text = stringResource(R.string.follow_system),
                    selected = locale == SYSTEM_DEFAULT
                ) { changeLanguage(SYSTEM_DEFAULT) }
            }
            for (language in Prefs.languages) {
                item {
                    PreferenceSingleChoiceItem(
                        text = Prefs.getLanguageDesc(language.key),
                        selected = locale == language.key
                    ) {
                        changeLanguage(language.key)
                    }
                }
            }
        }
    }
}

