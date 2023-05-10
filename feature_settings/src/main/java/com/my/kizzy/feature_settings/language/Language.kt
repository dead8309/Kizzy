package com.my.kizzy.feature_settings.language

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.my.kizzy.resources.R
import com.my.kizzy.preference.*
import com.my.kizzy.preference.Prefs.LANGUAGE
import com.my.kizzy.ui.components.BackButton
import com.my.kizzy.ui.components.preference.PreferenceSingleChoiceItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Language(
    onBackPressed: () -> Unit,
    updateLocaleLanguage: (String) -> Unit
) {
    var locale by remember { mutableStateOf(getLanguageNumber()) }

    fun changeLanguage(index: Int) {
        locale = index
        Prefs[LANGUAGE] = locale
        updateLocaleLanguage(getLanguageConfig())
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

