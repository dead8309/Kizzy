/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * Subtitle.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@SuppressLint("ModifierParameter")
@Composable
fun Subtitle(
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(15.dp, 5.dp),
    text: String,
    color: Color = MaterialTheme.colorScheme.primary,
    style: TextStyle = MaterialTheme.typography.labelLarge
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        style = style
    )
}