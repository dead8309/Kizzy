package com.my.kizzy.ui.screen.settings.language

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.my.kizzy.MainActivity
import com.kizzy.strings.R
import com.my.kizzy.preference.*
import com.my.kizzy.preference.Prefs
import com.my.kizzy.ui.components.BackButton
import com.my.kizzy.ui.components.preference.PreferenceSingleChoiceItem
import com.my.kizzy.preference.Prefs.LANGUAGE
import com.my.kizzy.preference.SYSTEM_DEFAULT
import com.my.kizzy.preference.getLanguageConfig
import com.my.kizzy.preference.getLanguageDesc
import com.my.kizzy.preference.getLanguageNumber
import com.my.kizzy.preference.languages

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Language(onBackPressed: () -> Unit) {
    var locale by remember { mutableStateOf(getLanguageNumber()) }

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
            for (language in languages) {
                item {
                    PreferenceSingleChoiceItem(
                        text = getLanguageDesc(language.key),
                        selected = locale == language.key
                    ) {
                        changeLanguage(language.key)
                    }
                }
            }
        }
    }
}

