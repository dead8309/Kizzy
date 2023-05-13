/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * RpcField.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.ui.components

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RpcField(
    value: String,
    enabled: Boolean = true,
    trailingIcon: @Composable (() -> Unit)? = null,
    @StringRes label: Int,
    isError: Boolean = false,
    errorMessage: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
    content: @Composable (() -> Unit) = {},
    onValueChange: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        TextField(modifier = Modifier
            .fillMaxWidth(),
            value = value,
            onValueChange = {
                onValueChange(it)
            },
            enabled = enabled,
            label = { Text(stringResource(id = label)) },
            keyboardOptions = keyboardOptions,
            trailingIcon = trailingIcon,
            isError = isError
        )
        AnimatedVisibility(visible = isError) {
            Text(
                text = errorMessage,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
                color = MaterialTheme.colorScheme.error
            )
        }
        content()
    }
}