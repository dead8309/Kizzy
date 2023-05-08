/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * ProfileNetworkError.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.feature_profile.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ProfileNetworkError(
    modifier: Modifier,
    error: String
) {
    ElevatedCard(
        modifier = modifier,
        colors = CardDefaults.elevatedCardColors(
            containerColor = Color(0xFFFFDB92)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .drawWithCache {
                    onDrawBehind {
                        drawRect(
                            color = Color(0xFFFFBC41),
                            topLeft = Offset.Zero,
                            size = Size(15f, size.height),
                        )
                    }
                }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Outlined.Warning,
                    "networkError",
                    modifier = Modifier.size(32.dp),
                    tint = Color(0xFFCE8500)
                )
                Text(
                    text = "Could not Update User Profile:\n$error",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFCE8500)
                    )
                )
            }
        }
    }
}

@Preview
@Composable
fun Preview_Profile_Network_Error_Card() {
    ProfileNetworkError(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        error = "No Internet Connection. Try again after some time"
    )
}