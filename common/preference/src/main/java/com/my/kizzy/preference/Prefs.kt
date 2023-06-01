/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * Prefs.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.preference

import com.my.kizzy.domain.model.user.User
import com.tencent.mmkv.MMKV
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

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
        val enabledPackages: ArrayList<String> = Json.decodeFromString(apps)
        return enabledPackages.contains(packageName)
    }

    fun saveToPrefs(pkg: String) {
        val apps = get(ENABLED_APPS, "[]")
        val enabledPackages: ArrayList<String> = Json.decodeFromString(apps)
        if (enabledPackages.contains(pkg))
            enabledPackages.remove(pkg)
        else
            enabledPackages.add(pkg)

        set(ENABLED_APPS, Json.encodeToString(enabledPackages))
    }

    fun getUser(): User? {
        val userJson = get(USER_DATA,"")
        return when {
            userJson.isNotEmpty() -> Json.decodeFromString(userJson)
            else -> null
        }
    }
    //User Preferences
    const val USER_DATA = "user" //Json Data Referencing User_Data class
    const val TOKEN = "token"
    const val USER_ID = "user-id"
    const val USER_BIO = "user-bio"
    const val USER_NITRO = "user-nitro"
    const val LAST_RUN_CONSOLE_RPC = "last_run_console_rpc"
    const val LAST_RUN_CUSTOM_RPC = "last_run_custom_rpc"
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
    const val CUSTOM_THEME_COLOR = "custom_theme_color"
    const val IS_FIRST_LAUNCHED = "is_first_launched"
    const val CUSTOM_ACTIVITY_TYPE = "custom_activity_type"
    const val SHOW_LOGS_IN_COMPACT_MODE = "logs_compact_mode"

    const val PALETTE_STYLE = "palette_style"

    const val APPLY_FIELDS_FROM_LAST_RUN_RPC = "enable_setting_from_last_config"
}
