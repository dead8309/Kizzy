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

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.my.kizzy.resources.R
import com.my.kizzy.ui.theme.DISCORD_GREY

@Composable
internal fun DiscordLoginButton(
    onClick: () -> Unit,
    enabled: Boolean
) {
    if (enabled) {
        ElevatedButton(
            onClick = onClick,
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
