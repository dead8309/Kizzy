package com.my.kizzy.ui.screen.media

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.my.kizzy.R
import com.my.kizzy.service.AppDetectionService
import com.my.kizzy.service.CustomRpcService
import com.my.kizzy.service.MediaRpcService
import com.my.kizzy.ui.common.BackButton
import com.my.kizzy.ui.common.PreferenceSwitch
import com.my.kizzy.ui.common.SettingItem
import com.my.kizzy.ui.common.SwitchBar
import com.my.kizzy.utils.AppUtils
import com.my.kizzy.utils.Prefs
import com.my.kizzy.utils.Prefs.MEDIA_RPC_APP_ICON
import com.my.kizzy.utils.Prefs.MEDIA_RPC_ARTIST_NAME
import com.my.kizzy.utils.Prefs.MEDIA_RPC_ENABLE_TIMESTAMPS
import com.my.kizzy.utils.Prefs.RPC_USE_CUSTOM_WEBHOOK
import com.my.kizzy.utils.Prefs.RPC_USE_LOW_RES_ICON
import com.my.kizzy.utils.Prefs.SAVED_IMAGES

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaRPC(onBackPressed: () -> Unit) {
    val context = LocalContext.current
    var mediarpcRunning by remember { mutableStateOf(AppUtils.mediaRpcRunning(context)) }
    var isArtistEnabled by remember { mutableStateOf(Prefs[MEDIA_RPC_ARTIST_NAME,false]) }
    var isAppIconEnabled by remember { mutableStateOf(Prefs[MEDIA_RPC_APP_ICON,false]) }
    var isTimestampsEnabled by remember { mutableStateOf(Prefs[MEDIA_RPC_ENABLE_TIMESTAMPS,false]) }
    var isLowResIconsEnabled by remember { mutableStateOf(Prefs[RPC_USE_LOW_RES_ICON,false]) }
    var useCustomWebhook by remember { mutableStateOf(Prefs[RPC_USE_CUSTOM_WEBHOOK,""]) }
    var showAlertDialog by remember { mutableStateOf(hasNotificationAccess(context)) }
    var dismiss by remember { mutableStateOf(false) }
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
                navigationIcon = { BackButton { onBackPressed()} }
            )
        }
    ) {

        Column(modifier = Modifier.padding(it)) {
            SwitchBar(
                title = stringResource(id = R.string.enable_mediaRpc),
                checked = mediarpcRunning
            ) {
                mediarpcRunning = !mediarpcRunning
                when (mediarpcRunning) {
                    true -> {
                        context.stopService(Intent(context, AppDetectionService::class.java))
                        context.stopService(Intent(context, CustomRpcService::class.java))
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
                item {
                    PreferenceSwitch(
                        title = stringResource(id = R.string.use_low_res_icon),
                        description = stringResource(id = R.string.use_low_res_icon_desc),
                        icon = Icons.Default.HighQuality,
                        isChecked = isLowResIconsEnabled,
                    ) {
                        isLowResIconsEnabled = !isLowResIconsEnabled
                        Prefs[RPC_USE_LOW_RES_ICON] = isLowResIconsEnabled
                    }
                }


                item {
                    SettingItem(
                        title = stringResource(id = R.string.use_your_own_webhook),
                        description = stringResource(id = R.string.use_your_own_webhook_desc),
                        icon = Icons.Default.Webhook,
                    ) {
                        dismiss = !dismiss
                    }
                }
                item {
                    SettingItem(
                        title = stringResource(id = R.string.delete_saved_icon_urls),
                        description = stringResource(id = R.string.delete_saved_icon_urls_desc),
                        icon = Icons.Default.DeleteForever
                    ) {
                        Prefs.remove(SAVED_IMAGES)
                        Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            if (dismiss) {
                AlertDialog(
                    onDismissRequest = { dismiss = !dismiss },
                    confirmButton = {
                        TextButton(onClick = {
                            Prefs[RPC_USE_CUSTOM_WEBHOOK] = "$useCustomWebhook?wait=true"
                        }) {
                            Text(text = "Save")
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null
                        )
                    },
                    title = { Text(stringResource(id = R.string.edit_webhook_url)) },
                    text = {
                        TextField(
                            value = useCustomWebhook,
                            onValueChange = { new ->
                                useCustomWebhook = new
                            }
                        )
                    }
                )
            }

            if (!showAlertDialog) {
                with(MaterialTheme.colorScheme) {
                    AlertDialog(
                        onDismissRequest = { showAlertDialog = !showAlertDialog },
                        confirmButton = {
                            TextButton(onClick = {
                                context.startActivity(
                                    Intent(
                                        Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS
                                    )
                                )
                            }) {
                                Text(text = stringResource(android.R.string.ok))
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null
                            )
                        },
                        title = { Text("Permission Required") },
                        text = { Text(text = "Notification Access is needed for app to extract media information ") },
                        containerColor = errorContainer,
                        iconContentColor = error,
                        titleContentColor = onErrorContainer,
                        textContentColor = onErrorContainer,
                    )
                }
            }

        }
    }
}

private fun hasNotificationAccess(context: Context): Boolean {
    val enabledNotificationListeners = Settings.Secure.getString(
        context.contentResolver, "enabled_notification_listeners"
    )
    return enabledNotificationListeners != null && enabledNotificationListeners.contains(context.packageName)
}
