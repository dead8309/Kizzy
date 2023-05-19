/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * SheetItem.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.feature_custom_rpc.components.sheet

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun SheetItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    SheetItemImpl(
        onClick = onClick,
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 8.dp, end = 16.dp)
                    .size(28.dp),
                tint = MaterialTheme.colorScheme.secondary
            )
        },
        title = title
    )
}

@Composable
fun SheetItem(
    title: String,
    @DrawableRes icon: Int,
    onClick: () -> Unit
) {
    SheetItemImpl(
        onClick = onClick,
        icon = {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 8.dp, end = 16.dp)
                    .size(28.dp
                       /* Icons.Outlined.FileOpen.defaultWidth,
                        Icons.Outlined.FileOpen.defaultHeight
                    */),
                tint = MaterialTheme.colorScheme.secondary
            )
        },
        title = title
    )
}

@Composable
internal fun SheetItemImpl(
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    title: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            icon()
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                fontWeight = FontWeight.Medium,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}