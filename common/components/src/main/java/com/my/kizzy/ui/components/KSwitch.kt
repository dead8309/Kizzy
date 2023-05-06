/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * KSwitch.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun KSwitch(
    modifier: Modifier = Modifier,
    checked: Boolean,
    enable: Boolean = true,
    onClick: (() -> Unit)? = null,
) {
    Surface(
        modifier = modifier
            .requiredSize(56.dp, 28.dp)
            .alpha(if (enable) 1f else 0.5f),
        shape = CircleShape,
        color = animateColorAsState(
            with(MaterialTheme.colorScheme){
                if (checked) primary
                else surfaceVariant
            }
        ).value
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
                    then if (onClick != null) Modifier.clickable { onClick() } else Modifier
        ) {
            Surface(
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.CenterStart)
                    .offset(x = animateDpAsState(if (checked) 32.dp else 4.dp).value),
                shape = CircleShape,
                color = animateColorAsState(
                    with(MaterialTheme.colorScheme){
                        if (checked) onPrimary
                        else outline
                    }
                ).value
            ) {}
        }
    }
}

@Preview
@Composable
fun KSwitchPreview() {
    var switchState by remember { mutableStateOf(true) }
    KSwitch(checked = switchState, enable = switchState, onClick = {
        switchState =!switchState
    })
}