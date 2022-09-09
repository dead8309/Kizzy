package com.my.kizzy.ui.screen.media

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.my.kizzy.service.MediaRpcService
import com.my.kizzy.ui.common.BackButton
import com.my.kizzy.ui.common.PreferenceSwitch
import com.my.kizzy.ui.common.SwitchBar
import com.my.kizzy.utils.AppUtils
import com.my.kizzy.utils.Prefs
import com.my.kizzy.utils.Prefs.MEDIA_RPC_APP_ICON
import com.my.kizzy.utils.Prefs.MEDIA_RPC_ARTIST_NAME

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaRPC(onBackPressed: () -> Unit) {
    val context = LocalContext.current
    var mediarpcRunning by remember { mutableStateOf(AppUtils.mediaRpcRunning(context)) }
    var isArtistEnabled by remember { mutableStateOf(Prefs[MEDIA_RPC_ARTIST_NAME,false]) }
    var isAppIconEnabled by remember { mutableStateOf(Prefs[MEDIA_RPC_APP_ICON,false]) }
    var showAlertDialog by remember { mutableStateOf(hasNotificationAccess(context)) }
    
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

        Column(modifier = Modifier.padding(it)){
            SwitchBar(
                title = "Enable Media Rpc",
                checked = mediarpcRunning
            ) {
                mediarpcRunning = !mediarpcRunning
                context.startService(Intent(context, MediaRpcService::class.java))
            }
            
            PreferenceSwitch(
                title = "Enable Artist Name",
            icon = Icons.Default.Audiotrack,
            isChecked = isArtistEnabled,
            ){
                isArtistEnabled = !isArtistEnabled
                Prefs[MEDIA_RPC_ARTIST_NAME] = isArtistEnabled
            }

            PreferenceSwitch(
                title = "Show App Icon",
                icon = Icons.Default.Apps,
                isChecked = isAppIconEnabled,
            ){
                isAppIconEnabled = !isAppIconEnabled
                Prefs[MEDIA_RPC_APP_ICON] = isAppIconEnabled
            }

            if(!showAlertDialog) {
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
