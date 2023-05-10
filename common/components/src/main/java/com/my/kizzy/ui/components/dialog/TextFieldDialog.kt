/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * TextFieldDialog.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.ui.components.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import com.my.kizzy.resources.R
import com.my.kizzy.ui.components.ClipboardTextField

@Composable
fun TextFieldDialog(
    modifier: Modifier = Modifier,
    visible: Boolean = false,
    readOnly: Boolean = false,
    title: String = "",
    icon: ImageVector? = null,
    value: String = "",
    placeholder: String = "",
    errorText: String = "",
    dismissText: String = stringResource(R.string.cancel),
    confirmText: String = stringResource(R.string.confirm),
    onValueChange: (String) -> Unit = {},
    onDismissRequest: () -> Unit = {},
    onConfirm: (String) -> Unit = {},
    imeAction: ImeAction = ImeAction.Done,
) {
    val focusManager = LocalFocusManager.current
    if (visible) {
        AlertDialog(
            modifier = modifier,
            onDismissRequest = onDismissRequest,
            icon = {
                icon?.let {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                    )
                }
            },
            title = {
                Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis)
            },
            text = {
                ClipboardTextField(
                    modifier = modifier,
                    readOnly = readOnly,
                    value = value,
                    onValueChange = onValueChange,
                    placeholder = placeholder,
                    errorText = errorText,
                    imeAction = imeAction,
                    focusManager = focusManager,
                    onConfirm = onConfirm,
                )
            },
            confirmButton = {
                TextButton(
                    enabled = value.isNotBlank(),
                    onClick = {
                        focusManager.clearFocus()
                        onConfirm(value)
                    }
                ) {
                    Text(
                        text = confirmText,
                        color = if (value.isNotBlank()) {
                            Color.Unspecified
                        } else {
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)
                        }
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(text = dismissText)
                }
            },
        )
    }
}