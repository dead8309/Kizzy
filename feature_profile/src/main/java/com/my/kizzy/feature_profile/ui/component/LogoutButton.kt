/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * LogoutButton.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.feature_profile.ui.component

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape

@Composable
fun Logout(
    modifier: Modifier = Modifier,
    shape: Shape,
    onClicked: () -> Unit,
) {
    Button(
        modifier = modifier,
        shape = shape,
        onClick = { onClicked() },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        )
    ) {
        Text(text = "Logout")
    }
}