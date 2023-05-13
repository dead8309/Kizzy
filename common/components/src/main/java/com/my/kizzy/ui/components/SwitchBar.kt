/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * SwitchBar.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.my.kizzy.ui.theme.getColorScheme

@Composable
fun SwitchBar(
    title: String,
    isChecked: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val colorScheme = getColorScheme(darkTheme = false)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(25.dp))
            .background(colorScheme.primaryContainer)
            .toggleable(enabled){
                onClick()
            }
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        with(MaterialTheme) {
            Text(
                modifier = Modifier.weight(4f),
                text = title,
                maxLines = 1,
                style = typography.titleLarge.copy(fontSize = 20.sp),
                color = colorScheme.onSurface,
                overflow = TextOverflow.Ellipsis
            )
            KSwitch(
                modifier = Modifier.weight(1f),
                checked = isChecked,
                enable = enabled
            ){
                if (enabled) onClick()
            }
        }
    }
}

@Preview
@Composable
fun PreviewSwitchBar() {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black), contentAlignment = Alignment.Center){
        var state by remember { mutableStateOf(false) }
        SwitchBar(title = "SwitchBar", isChecked = state) {
            state = !state
        }
    }
}