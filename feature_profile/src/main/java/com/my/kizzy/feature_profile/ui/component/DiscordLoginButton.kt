/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * DiscordLoginButton.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.feature_profile.ui.component

import android.content.Context
import android.telephony.TelephonyManager
import android.app.Activity
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.my.kizzy.resources.R
import com.my.kizzy.ui.theme.DISCORD_GREY
import java.util.Locale

private val NON_ACCESSIBLE_COUNTRIES = listOf("TR", "RU")

@Composable
internal fun DiscordLoginButton(
    onClick: () -> Unit,
    enabled: Boolean
) {
    val context = LocalContext.current
    var showVpnDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val country = getUserCountry(context)
        if (country in NON_ACCESSIBLE_COUNTRIES) {
            showVpnDialog = true
        }
    }

    if (showVpnDialog) {
        AlertDialog(
            onDismissRequest = { showVpnDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    showVpnDialog = false
                    onClick()
                }) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showVpnDialog = false
                    (context as? Activity)?.finish()
                }) {
                    Text(stringResource(R.string.exit))
                }
            },
            text = {
                Text(stringResource(R.string.use_a_vpn_desc))
            }
        )
    }

    if (enabled) {
        ElevatedButton(
            onClick = {
                if (showVpnDialog) {
                    showVpnDialog = true
                } else {
                    onClick()
                }
            },
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = DISCORD_GREY,
                contentColor = Color.White.copy(alpha = 0.8f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_discord),
                tint = Color.Unspecified,
                contentDescription = "discord_login",
                modifier = Modifier.padding(end = 5.dp)
            )
            Text(text = stringResource(id = R.string.login_with_discord))
        }
    }
}

private fun getUserCountry(context: Context): String {
    val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    val country = tm.networkCountryIso
    return if (country.isNullOrEmpty()) {
        Locale.getDefault().country.uppercase()
    } else {
        country.uppercase()
    }
}