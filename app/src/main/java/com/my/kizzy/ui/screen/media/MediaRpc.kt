package com.my.kizzy.ui.screen.media

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.my.kizzy.R
import com.my.kizzy.service.AppDetectionService
import com.my.kizzy.service.CustomRpcService
import com.my.kizzy.service.ExperimentalRpc
import com.my.kizzy.service.MediaRpcService
import com.my.kizzy.ui.common.BackButton
import com.my.kizzy.ui.common.PreferenceSwitch
import com.my.kizzy.ui.common.PreferencesHint
import com.my.kizzy.ui.common.SwitchBar
import com.my.kizzy.utils.AppUtils
import com.my.kizzy.utils.Prefs
import com.my.kizzy.utils.Prefs.MEDIA_RPC_APP_ICON
import com.my.kizzy.utils.Prefs.MEDIA_RPC_ARTIST_NAME
import com.my.kizzy.utils.Prefs.MEDIA_RPC_ENABLE_TIMESTAMPS

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaRPC(onBackPressed: () -> Unit) {
    val context = LocalContext.current
    var mediarpcRunning by remember { mutableStateOf(AppUtils.mediaRpcRunning()) }
    var isArtistEnabled by remember { mutableStateOf(Prefs[MEDIA_RPC_ARTIST_NAME, false]) }
    var isAppIconEnabled by remember { mutableStateOf(Prefs[MEDIA_RPC_APP_ICON, false]) }
    var isTimestampsEnabled by remember { mutableStateOf(Prefs[MEDIA_RPC_ENABLE_TIMESTAMPS, false]) }
    var hasNotificationAccess by remember { mutableStateOf(context.hasNotificationAccess()) }
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "Media Rpc",
                        style = MaterialTheme.typography.headlineLarge,
                    )
                },
                navigationIcon = { BackButton { onBackPressed() } }
            )
        }
    ) {

        Column(modifier = Modifier.padding(it)) {
            AnimatedVisibility(visible = !hasNotificationAccess
            ) {
                PreferencesHint(
                    title = "Permission Required",
                    description = "Notification Access is needed for app to extract media information",
                    icon = Icons.Default.Warning,
                ) {
                    when (context.hasNotificationAccess()) {
                        true -> hasNotificationAccess = true
                        false -> context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                    }
                }
            }
            SwitchBar(
                title = stringResource(id = R.string.enable_mediaRpc),
                isChecked = mediarpcRunning,
                enabled = hasNotificationAccess
            ) {
                mediarpcRunning = !mediarpcRunning
                when (mediarpcRunning) {
                    true -> {
                        context.stopService(Intent(context, AppDetectionService::class.java))
                        context.stopService(Intent(context, CustomRpcService::class.java))
                        context.stopService(Intent(context, ExperimentalRpc::class.java))
                        context.startService(Intent(context, MediaRpcService::class.java))
                    }
                    false -> context.stopService(Intent(context, MediaRpcService::class.java))
                }
            }
            LazyColumn {
                item {
                    PreferenceSwitch(
                        title = stringResource(id = R.string.enable_artist_name),
                        icon = Icons.Default.Audiotrack,
                        isChecked = isArtistEnabled,
                    ) {
                        isArtistEnabled = !isArtistEnabled
                        Prefs[MEDIA_RPC_ARTIST_NAME] = isArtistEnabled
                    }
                }
                item {
                    PreferenceSwitch(
                        title = stringResource(id = R.string.show_app_icon),
                        icon = Icons.Default.Apps,
                        isChecked = isAppIconEnabled,
                    ) {
                        isAppIconEnabled = !isAppIconEnabled
                        Prefs[MEDIA_RPC_APP_ICON] = isAppIconEnabled
                    }
                }
                item {
                    PreferenceSwitch(
                        title = stringResource(id = R.string.enable_timestamps),
                        icon = Icons.Default.Timer,
                        isChecked = isTimestampsEnabled,
                    ) {
                        isTimestampsEnabled = !isTimestampsEnabled
                        Prefs[MEDIA_RPC_ENABLE_TIMESTAMPS] = isTimestampsEnabled
                    }
                }
            }
        }
    }
}

fun Context.hasNotificationAccess(): Boolean {
    val enabledNotificationListeners = Settings.Secure.getString(
        this.contentResolver, "enabled_notification_listeners"
    )
    return enabledNotificationListeners != null && enabledNotificationListeners.contains(this.packageName)
}
