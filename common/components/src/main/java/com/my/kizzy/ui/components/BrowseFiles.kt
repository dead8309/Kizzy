/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * BrowseFiles.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BrowseFilesButton(
    modifier: Modifier,
    onClick: () -> Unit) {
    Button(
        onClick = {
            onClick()
        },
        modifier = modifier,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text = "Browse Files")
        Spacer(modifier = Modifier.width(5.dp))
        Icon(
            Icons.Default.OpenInNew,
            null
        )
    }
}