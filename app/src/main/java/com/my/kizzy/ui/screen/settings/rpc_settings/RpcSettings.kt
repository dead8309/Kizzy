package com.my.kizzy.ui.screen.settings.rpc_settings

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Webhook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.my.kizzy.R
import com.my.kizzy.ui.common.BackButton
import com.my.kizzy.ui.common.PreferenceSwitch
import com.my.kizzy.ui.common.SettingItem
import com.my.kizzy.utils.Prefs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RpcSettings(onBackPressed: () -> Boolean) {
    val context = LocalContext.current
    var isLowResIconsEnabled by remember { mutableStateOf(Prefs[Prefs.RPC_USE_LOW_RES_ICON, false]) }
    var useCustomWebhook by remember { mutableStateOf(Prefs[Prefs.RPC_USE_CUSTOM_WEBHOOK, ""]) }
    var dismiss by remember { mutableStateOf(false) }
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.rpc_settings),
                        style = MaterialTheme.typography.headlineLarge,
                    )
                },
                navigationIcon = { BackButton { onBackPressed() } }
            )
        }
    ){
        LazyColumn(modifier = Modifier.padding(it)){
            item {
                PreferenceSwitch(
                    title = stringResource(id = R.string.use_low_res_icon),
                    description = stringResource(id = R.string.use_low_res_icon_desc),
                    icon = Icons.Default.HighQuality,
                    isChecked = isLowResIconsEnabled,
                ) {
                    isLowResIconsEnabled = !isLowResIconsEnabled
                    Prefs[Prefs.RPC_USE_LOW_RES_ICON] = isLowResIconsEnabled
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
                    Prefs.remove(Prefs.SAVED_IMAGES)
                    Prefs.remove(Prefs.SAVED_ARTWORK)
                    Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show()
                }
            }
        }
        if (dismiss) {
            AlertDialog(
                onDismissRequest = { dismiss = !dismiss },
                confirmButton = {
                    TextButton(onClick = {
                        if(useCustomWebhook.startsWith("https://discord.com/api/webhooks"))
                           Prefs[Prefs.RPC_USE_CUSTOM_WEBHOOK] = "$useCustomWebhook?wait=true"
                        else Prefs.remove(Prefs.RPC_USE_CUSTOM_WEBHOOK)
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
    }
}
