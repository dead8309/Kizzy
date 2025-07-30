/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * RpcFieldWithCompletion.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.ui.components

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties

private const val MIN_CHARS_TO_TRIGGER = 2
private const val TEMPLATE_REGEX = "\\{\\{[^{}]*\\}\\}"
private const val MAX_COMPLETION_LIST_HEIGHT = 250
private const val SINGLE_COMPLETION_ITEM_HEIGHT = 62.5

private typealias ResourceId = Int

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RpcFieldWithCompletions(
    value: String,
    enabled: Boolean = true,
    trailingIcon: @Composable (() -> Unit)? = null,
    @StringRes label: Int,
    isError: Boolean = false,
    errorMessage: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
    content: @Composable (() -> Unit) = {},
    onValueChange: (String) -> Unit = {},
    completionList: List<Pair<String, ResourceId>> = emptyList(),
) {
    var textFieldValue by remember { mutableStateOf(TextFieldValue(value)) }
    var showSuggestions by remember { mutableStateOf(false) }
    var filteredPlaceholders by remember { mutableStateOf(completionList) }
    var lastTemplateCursorPosition by rememberSaveable { mutableIntStateOf(-1) }
    var lastTemplateText by rememberSaveable { mutableStateOf("") }
    var internalIsFocused by remember { mutableStateOf(false) }

    val density = LocalDensity.current

    LaunchedEffect(value) {
        if (value != textFieldValue.text) {
            textFieldValue = textFieldValue.copy(text = value)
        }
    }

    // check for template state on recomposition
    val checkTemplateState = {
        val cursor = textFieldValue.selection.start
        val text = textFieldValue.text

        if (cursor > 0 && cursor <= text.length) {
            val textBefore = text.take(cursor)
            val templateStartIndex = textBefore.lastIndexOf("{{")

            if (templateStartIndex != -1 && !textBefore.substring(templateStartIndex)
                    .contains("}}")
            ) {
                // in a template
                lastTemplateCursorPosition = cursor
                lastTemplateText = text

                val partialTemplate = textBefore.substring(templateStartIndex + 2)

                filteredPlaceholders = if (partialTemplate.isNotEmpty()) {
                    completionList.filter { (key, _) ->
                        val keyToMatch = if (key.startsWith("{{") && key.endsWith("}}")) {
                            key.substring(2, key.length - 2)
                        } else {
                            key
                        }
                        keyToMatch.contains(partialTemplate, ignoreCase = true)
                    }
                } else {
                    completionList
                }

                showSuggestions = true
            } else if (lastTemplateCursorPosition != -1 && text == lastTemplateText) {
                // text hasn't changed but cursor might have moved due to keyboard change
                // keep suggestions visible
                showSuggestions = showSuggestions && filteredPlaceholders.isNotEmpty()
            } else {
                // check for regular word
                val currentWord = getCurrentWord(textBefore)
                if (currentWord.length >= MIN_CHARS_TO_TRIGGER) {
                    filteredPlaceholders = completionList.filter { (key, _) ->
                        key.contains(currentWord, ignoreCase = true)
                    }
                    showSuggestions = filteredPlaceholders.isNotEmpty()
                } else {
                    // reset template tracking
                    lastTemplateCursorPosition = -1
                    lastTemplateText = ""
                    showSuggestions = false
                }
            }
        } else {
            lastTemplateCursorPosition = -1
            lastTemplateText = ""
            showSuggestions = false
        }
    }

    val color = MaterialTheme.colorScheme.primary
    val background = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
    val visualTransformation = remember(color, background) {
        VisualTransformation { text ->
            val annotatedString = buildAnnotatedString {
                append(text)
                TEMPLATE_REGEX.toRegex().findAll(text).forEach { matchResult ->
                    addStyle(
                        style = SpanStyle(
                            color = color,
                            background = background,
                        ), start = matchResult.range.first, end = matchResult.range.last + 1
                    )
                }
            }

            TransformedText(
                text = annotatedString, offsetMapping = OffsetMapping.Identity
            )
        }
    }

    /**
     * Call on each composition to ensure suggestions state is maintained
     *
     * Why ?
     * While testing, i noticed when you switch between number to text keyboard,
     * the suggestions would disappear even if the text was still in a template.
     **/
    checkTemplateState()

    LocalContext.current
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .onFocusChanged { focusState ->
                    internalIsFocused = focusState.isFocused
                    if (!internalIsFocused) {
                        showSuggestions = false
                        lastTemplateCursorPosition = -1
                        lastTemplateText = ""
                    }
                },
            value = textFieldValue,
            visualTransformation = visualTransformation,
            onValueChange = { newTextFieldValue ->
                textFieldValue = newTextFieldValue
                onValueChange(newTextFieldValue.text)
                checkTemplateState()
            },
            enabled = enabled,
            label = { Text(stringResource(id = label)) },
            placeholder = { Text("Type '{{' to insert a variable") },
            keyboardOptions = keyboardOptions,
            trailingIcon = trailingIcon,
            isError = isError,
            singleLine = true,
        )

        AnimatedVisibility(
            visible = isError, modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            Text(
                text = errorMessage,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                textAlign = TextAlign.End,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (showSuggestions && filteredPlaceholders.isNotEmpty() && internalIsFocused) {
            Popup(
                alignment = Alignment.TopCenter,
                offset = IntOffset(
                    0, with(density) {
                        val offset = minOf(
                            MAX_COMPLETION_LIST_HEIGHT.dp.roundToPx(),
                            (SINGLE_COMPLETION_ITEM_HEIGHT * filteredPlaceholders.size).dp.roundToPx()
                        )
                        // We are displaying popup above the TextField
                        -offset
                    }),
                properties = PopupProperties(
                    focusable = false,
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true
                ),
                onDismissRequest = { showSuggestions = false }
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f)
                    ),
                    border = BorderStroke(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.20f)
                    )
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .heightIn(max = MAX_COMPLETION_LIST_HEIGHT.dp)
                    ) {
                        items(filteredPlaceholders.size) { index ->
                            val (placeholder, resId) = filteredPlaceholders[index]
                            var isHovered by remember { mutableStateOf(false) }
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        if (isHovered) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                                        else MaterialTheme.colorScheme.surface
                                    )
                                    .clickable {
                                        val currentSelectionStart = textFieldValue.selection.start
                                        val textUpToCursor =
                                            textFieldValue.text.take(currentSelectionStart)
                                        val actualKey =
                                            if (placeholder.startsWith("{{") && placeholder.endsWith(
                                                    "}}"
                                                )
                                            ) {
                                                placeholder.substring(2, placeholder.length - 2)
                                            } else {
                                                placeholder
                                            }
                                        val newText: String
                                        val newCursorPos: Int
                                        if (textUpToCursor.lastIndexOf("{{") != -1) {
                                            val templateStart = textUpToCursor.lastIndexOf("{{")
                                            val partialTemplate =
                                                textUpToCursor.substring(templateStart)
                                            if (partialTemplate.startsWith("{{") && !partialTemplate.contains(
                                                    "}}"
                                                )
                                            ) {
                                                val prefix =
                                                    textFieldValue.text.substring(0, templateStart)
                                                val suffix = textFieldValue.text.substring(
                                                    currentSelectionStart
                                                )
                                                newText = "$prefix{{$actualKey}}$suffix"
                                                newCursorPos =
                                                    prefix.length + actualKey.length + 4
                                            } else {
                                                val currentWord = getCurrentWord(textUpToCursor)
                                                if (currentWord.isNotEmpty()) {
                                                    val wordStartPos =
                                                        currentSelectionStart - currentWord.length
                                                    val prefix = textFieldValue.text.substring(
                                                        0,
                                                        wordStartPos
                                                    )
                                                    val suffix = textFieldValue.text.substring(
                                                        currentSelectionStart
                                                    )
                                                    newText = "$prefix{{$actualKey}}$suffix"
                                                    newCursorPos =
                                                        prefix.length + actualKey.length + 4
                                                } else {
                                                    val prefix = textUpToCursor
                                                    val suffix = textFieldValue.text.substring(
                                                        currentSelectionStart
                                                    )
                                                    newText = "$prefix{{$actualKey}}$suffix"
                                                    newCursorPos =
                                                        prefix.length + actualKey.length + 4
                                                }
                                            }
                                        } else {
                                            val currentWord = getCurrentWord(textUpToCursor)
                                            if (currentWord.isNotEmpty()) {
                                                val wordStartPos =
                                                    currentSelectionStart - currentWord.length
                                                val prefix =
                                                    textFieldValue.text.substring(0, wordStartPos)
                                                val suffix = textFieldValue.text.substring(
                                                    currentSelectionStart
                                                )
                                                newText = "$prefix{{$actualKey}}$suffix"
                                                newCursorPos = prefix.length + actualKey.length + 4
                                            } else {
                                                val prefix = textUpToCursor
                                                val suffix = textFieldValue.text.substring(
                                                    currentSelectionStart
                                                )
                                                newText = "$prefix{{$actualKey}}$suffix"
                                                newCursorPos = prefix.length + actualKey.length + 4
                                            }
                                        }
                                        textFieldValue = textFieldValue.copy(
                                            text = newText,
                                            selection = TextRange(newCursorPos)
                                        )
                                        onValueChange(newText)
                                        showSuggestions = false
                                    }
                                    .pointerInput(Unit) {
                                        awaitPointerEventScope {
                                            while (true) {
                                                val event = awaitPointerEvent()
                                                val isEnter = event.changes.any { it.pressed }
                                                isHovered = isEnter
                                            }
                                        }
                                    }
                                    .padding(horizontal = 18.dp, vertical = 12.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(
                                                MaterialTheme.colorScheme.primary,
                                                shape = CircleShape
                                            )
                                    )
                                    Text(
                                        text = placeholder,
                                        color = MaterialTheme.colorScheme.primary,
                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                        modifier = Modifier.padding(start = 12.dp)
                                    )
                                }
                                Text(
                                    text = stringResource(resId),
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(start = 20.dp, top = 2.dp)
                                )
                            }
                            if (index != filteredPlaceholders.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 12.dp),
                                    thickness = DividerDefaults.Thickness,
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    content()
}

private fun getCurrentWord(text: String): String {
    val wordPattern = "\\w+$"
    val matcher = Regex(wordPattern).find(text)
    return matcher?.value ?: ""
}

