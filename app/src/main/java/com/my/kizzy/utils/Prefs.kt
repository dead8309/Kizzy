package com.my.kizzy.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


object Prefs {

    private const val APP_PREFERENCES = "kizzy_preferences"
    lateinit var preferences: SharedPreferences

    fun init(context: Context) {
        preferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = this.edit()
        operation(editor)
        editor.apply()
    }
    fun remove(key: String){
        preferences.edit {
            it.remove(key)
        }
    }

    operator fun set(key: String, value: Any?) =
        when (value) {
            is String? -> preferences.edit { it.putString(key, value) }
            is Int -> preferences.edit { it.putInt(key, value) }
            is Boolean -> preferences.edit { it.putBoolean(key, value) }
            is Float -> preferences.edit { it.putFloat(key, value) }
            is Long -> preferences.edit { it.putLong(key, value) }
            else -> throw UnsupportedOperationException("Not yet implemented")
        }

    inline operator fun <reified T : Any> get(
        key: String,
        defaultValue: T? = null
    ): T =
        when (T::class) {
            String::class -> preferences.getString(key, defaultValue as String? ?: "") as T
            Int::class -> preferences.getInt(key, defaultValue as? Int ?: -1) as T
            Boolean::class -> preferences.getBoolean(key, defaultValue as? Boolean ?: false) as T
            Float::class -> preferences.getFloat(key, defaultValue as? Float ?: -1f) as T
            Long::class -> preferences.getLong(key, defaultValue as? Long ?: -1) as T
            else -> throw UnsupportedOperationException("Not yet implemented")
        }

    fun isAppEnabled(packageName: String?): Boolean {
        val apps = get(ENABLED_APPS, "[]")
        val enabled_packages: ArrayList<String> = Gson().fromJson(
            apps,
            object : TypeToken<ArrayList<String>?>() {}.type
        )
        return enabled_packages.contains(packageName)
    }

    fun saveToPrefs(pkg: String) {
        val apps = get(ENABLED_APPS, "[]")
        val enabled_packages: ArrayList<String> = Gson().fromJson(
            apps,
            object : TypeToken<ArrayList<String>?>() {}.type
        )
        if (enabled_packages.contains(pkg))
            enabled_packages.remove(pkg)
        else
            enabled_packages.add(pkg)

        set(ENABLED_APPS, Gson().toJson(enabled_packages))
    }

    //User Preferences
    const val USER_DATA ="user" //Json Data Referencing User_Data class
    const val TOKEN = "token"

    const val LANGUAGE = "language"

    const val ENABLED_APPS = "enabled_apps"

    //Media Rpc Preferences
    const val MEDIA_RPC_ARTIST_NAME = "media_rpc_artist_name"
    const val MEDIA_RPC_APP_ICON = "media_rpc_app_icon"
    const val MEDIA_RPC_ENABLE_TIMESTAMPS = "enable_timestamps"
    const val RPC_USE_LOW_RES_ICON = "use_low_res_app_icons"
    const val RPC_USE_CUSTOM_WEBHOOK = "use_custom_webhook"

    // Saved Image Asset ids
    const val SAVED_IMAGES = "saved_images"
    // Saved ArtWork
    const val SAVED_ARTWORK = "saved_artwork"
}
