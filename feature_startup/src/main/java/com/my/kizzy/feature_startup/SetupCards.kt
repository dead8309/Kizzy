/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * SetupCards.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.feature_startup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.my.kizzy.resources.R

@Composable
fun SetupCard(
    title: String = "Title ".repeat(2),
    description: String = "Description text ".repeat(3),
    status: Boolean,
    onClick: () -> Unit = {},
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(20.dp))
        .background(
            MaterialTheme.colorScheme.inverseOnSurface
        )
        .clickable { onClick() }
        .padding(horizontal = 12.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)) {
        with(MaterialTheme) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    maxLines = 1,
                    style = typography.titleLarge.copy(fontSize = 20.sp).copy(fontWeight = FontWeight.SemiBold),
                    color = colorScheme.onSecondaryContainer
                )
                Icon(
                    imageVector = Icons.Default.OpenInNew,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = colorScheme.primary
                )
            }
            Text(
                text = when (status) {
                    true -> stringResource(id = R.string.permission_granted)
                    false -> stringResource(id = R.string.permission_not_granted)
                },
                color = colorScheme.primary,
                style = typography.bodySmall.copy(fontWeight = FontWeight.W500),
            )
            Text(
                text = description,
                color = colorScheme.onSecondaryContainer,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                style = typography.bodyMedium,
            )
        }
    }
}


@Composable
fun SetupCard(
    title: String = "Title ".repeat(2),
    description: String = "Description text ".repeat(3),
    onClick: () -> Unit = {},
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(20.dp))
        .background(
            MaterialTheme.colorScheme.inverseOnSurface
        )
        .clickable { onClick() }
        .padding(horizontal = 12.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)) {
        with(MaterialTheme) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    maxLines = 1,
                    style = typography.titleLarge.copy(fontSize = 20.sp).copy(fontWeight = FontWeight.SemiBold),
                    color = colorScheme.onSecondaryContainer
                )
                Icon(
                    imageVector = Icons.Default.NavigateNext,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = colorScheme.primary
                )
            }
            Text(
                text = description,
                color = colorScheme.onSecondaryContainer,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = typography.bodyMedium,
            )
        }
    }
}