package com.my.kizzy.utils

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.my.kizzy.App.Companion.applicationScope
import com.my.kizzy.R
import com.my.kizzy.ui.theme.ColorScheme.DEFAULT_SEED_COLOR
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

object Prefs {
    val kv = MMKV.defaultMMKV()

    operator fun set(key: String, value: Any?) =
        when (value) {
            is String? -> kv.encode(key, value)
            is Int -> kv.encode(key, value)
            is Boolean -> kv.encode(key, value)
            is Float ->  kv.encode(key, value)
            is Long -> kv.encode(key, value)
            else -> throw UnsupportedOperationException("Not yet implemented")
        }

    inline operator fun <reified T : Any> get(
        key: String,
        defaultValue: T? = null
    ): T =
        when (T::class) {
            String::class -> kv.decodeString(key, defaultValue as String? ?: "") as T
            Int::class -> kv.decodeInt(key, defaultValue as? Int ?: -1) as T
            Boolean::class -> kv.decodeBool(key, defaultValue as? Boolean ?: false) as T
            Float::class -> kv.decodeFloat(key, defaultValue as? Float ?: -1f) as T
            Long::class -> kv.decodeLong(key, defaultValue as? Long ?: -1) as T
            else -> throw UnsupportedOperationException("Not yet implemented")
        }

    fun remove(key: String){
        kv.removeValueForKey(key)
    }

    fun isAppEnabled(packageName: String?): Boolean {
        val apps = get(ENABLED_APPS, "[]")
        val enabledPackages: ArrayList<String> = Gson().fromJson(
            apps,
            object : TypeToken<ArrayList<String>?>() {}.type
        )
        return enabledPackages.contains(packageName)
    }

    fun saveToPrefs(pkg: String) {
        val apps = get(ENABLED_APPS, "[]")
        val enabledPackages: ArrayList<String> = Gson().fromJson(
            apps,
            object : TypeToken<ArrayList<String>?>() {}.type
        )
        if (enabledPackages.contains(pkg))
            enabledPackages.remove(pkg)
        else
            enabledPackages.add(pkg)

        set(ENABLED_APPS, Gson().toJson(enabledPackages))
    }

    //User Preferences
    const val USER_DATA = "user" //Json Data Referencing User_Data class
    const val TOKEN = "token"
    const val USER_ID = "user-id"
    const val USER_BIO = "user-bio"
    const val USER_NITRO = "user-nitro"

    const val LANGUAGE = "language"
    const val ENABLED_APPS = "enabled_apps"

    //Media Rpc Preferences
    const val MEDIA_RPC_ARTIST_NAME = "media_rpc_artist_name"
    const val MEDIA_RPC_APP_ICON = "media_rpc_app_icon"
    const val MEDIA_RPC_ENABLE_TIMESTAMPS = "enable_timestamps"

    //Rpc Setting Preferences
    const val USE_RPC_BUTTONS = "use_saved_rpc_buttons"
    const val RPC_BUTTONS_DATA = "saved_rpc_buttons_data"
    const val RPC_USE_LOW_RES_ICON = "use_low_res_app_icons"
    const val CONFIGS_DIRECTORY = "configs_directory"
    // Saved Image Asset ids
    const val SAVED_IMAGES = "saved_images"
    // Saved ArtWork
    const val SAVED_ARTWORK = "saved_artwork"

    //new
    const val DARK_THEME = "dark_theme_value"
    const val HIGH_CONTRAST = "high_contrast"
    const val DYNAMIC_COLOR = "dynamic_color"
    const val THEME_COLOR = "theme_color"

    data class AppSettings(
        val darkTheme: DarkThemePreference = DarkThemePreference(),
        val isDynamicColorEnabled: Boolean = false,
        val seedColor: Int = DEFAULT_SEED_COLOR
    )

    private val mutableAppSettingsStateFlow = MutableStateFlow(
        AppSettings(
            DarkThemePreference(
                darkThemeValue = get(DARK_THEME, DarkThemePreference.FOLLOW_SYSTEM),
                isHighContrastModeEnabled = get(HIGH_CONTRAST, false)
            ),
            isDynamicColorEnabled = get(DYNAMIC_COLOR, false),
            seedColor = get(THEME_COLOR, DEFAULT_SEED_COLOR)
        )
    )
    val AppSettingsStateFlow = mutableAppSettingsStateFlow.asStateFlow()
    fun modifyDarkThemePreference(
        darkThemeValue: Int = AppSettingsStateFlow.value.darkTheme.darkThemeValue,
        isHighContrastModeEnabled: Boolean = AppSettingsStateFlow.value.darkTheme.isHighContrastModeEnabled
    ) {
        applicationScope.launch(Dispatchers.IO) {
            mutableAppSettingsStateFlow.update {
                it.copy(
                    darkTheme = AppSettingsStateFlow.value.darkTheme.copy(
                        darkThemeValue = darkThemeValue,
                        isHighContrastModeEnabled = isHighContrastModeEnabled
                    )
                )
            }
            set(DARK_THEME, darkThemeValue)
            set(HIGH_CONTRAST, isHighContrastModeEnabled)
        }
    }

    fun modifyThemeSeedColor(colorArgb: Int) {
        applicationScope.launch(Dispatchers.IO) {
            mutableAppSettingsStateFlow.update {
                it.copy(seedColor = colorArgb)
            }
            set(THEME_COLOR, colorArgb)
        }
    }

    fun switchDynamicColor(enabled: Boolean = !mutableAppSettingsStateFlow.value.isDynamicColorEnabled) {
        applicationScope.launch(Dispatchers.IO) {
            mutableAppSettingsStateFlow.update {
                it.copy(isDynamicColorEnabled = enabled)
            }
            set(DYNAMIC_COLOR, enabled)
        }
    }

    data class DarkThemePreference(
        val darkThemeValue: Int = FOLLOW_SYSTEM,
        val isHighContrastModeEnabled: Boolean = false
    ) {
        companion object {
            const val FOLLOW_SYSTEM = 1
            const val ON = 2
            const val OFF = 3
        }

        @Composable
        fun isDarkTheme(): Boolean {
            return if (darkThemeValue == FOLLOW_SYSTEM)
                isSystemInDarkTheme()
            else darkThemeValue == ON
        }

        @Composable
        fun getDarkThemeDesc(): String {
            return when (darkThemeValue) {
                FOLLOW_SYSTEM -> stringResource(R.string.follow_system)
                ON -> stringResource(R.string.on)
                else -> stringResource(R.string.off)
            }
        }
    }
}