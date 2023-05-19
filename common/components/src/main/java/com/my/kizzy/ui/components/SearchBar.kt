/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * SearchBar.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    text: String = "",
    placeholder: String = "",
    onClose: () -> Unit = {},
    onTextChanged: ((String) -> Unit)
) {
    BasicTextField(
        value = text,
        onValueChange = onTextChanged,
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            color = MaterialTheme.colorScheme.onSurface
        ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(200.dp)
            ),
        decorationBox = { innerTextField ->
            val containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp)
            TextFieldDefaults.DecorationBox(
                value = text,
                innerTextField = innerTextField,
                enabled = true,
                singleLine = true,
                visualTransformation = VisualTransformation.None,
                interactionSource = remember { MutableInteractionSource() },
                placeholder = { Text(placeholder) },
                trailingIcon = {
                    IconButton(onClick = {
                        if (text.isNotEmpty())
                            onTextChanged("")
                        else
                            onClose()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close Icon"
                        )
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = containerColor,
                    unfocusedContainerColor = containerColor,
                    disabledContainerColor = containerColor,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                contentPadding = PaddingValues(
                    start = 14.dp,
                    end = 12.dp,
                    top = 10.dp,
                    bottom = 10.dp
                ),
            )
        }
    )
}

@Preview
@Composable
fun Search() {
    SearchBar(
        text = "test"
    ) {
    }
}