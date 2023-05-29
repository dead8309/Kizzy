/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * Features.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.feature_home.feature

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RichTooltipBox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.SizeMode
import com.my.kizzy.resources.R
import com.my.kizzy.ui.components.KSwitch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Features(
    homeItems: List<HomeFeature> = emptyList(), onValueUpdate: (Int) -> Unit
) {
    val uriHandler = LocalUriHandler.current
    val brush = Brush.linearGradient(
        listOf(
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.secondaryContainer.copy(0.8f),
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
        )
    )
    val featureSize = (LocalConfiguration.current.screenWidthDp.dp / 2)
    FlowRow(
        mainAxisSize = SizeMode.Expand,
        mainAxisAlignment = FlowMainAxisAlignment.SpaceBetween
    ) {
        for (i in homeItems.indices) {
            RichTooltipBox(
                title = {
                    Text(homeItems[i].title, style = MaterialTheme.typography.titleMedium)
                },
                text = {
                    Text(homeItems[i].tooltipText)
                },
                action = {
                    TextButton(
                        onClick = {
                            uriHandler.openUri(homeItems[i].featureDocsLink)
                        },
                    ) {
                        Text(
                            text = "Learn more",
                        )
                    }
                },
            ) {
                Box(modifier = Modifier
                    .then(
                        if (homeItems[i].tooltipText.isNotBlank()){
                            Modifier.tooltipTrigger()
                        } else
                            Modifier
                    )
                    .size(featureSize)
                    .padding(9.dp)
                    .aspectRatio(1f)
                    .clip(homeItems[i].shape)
                    .background(
                        brush = if (homeItems[i].isChecked) {
                            Brush.linearGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    MaterialTheme.colorScheme.primaryContainer.copy(0.8f),
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                                )
                            )
                        } else {
                            brush
                        }
                    )
                    .clickable { homeItems[i].route?.let { homeItems[i].onClick(it) } }) {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(15.dp, 15.dp, 2.dp, 15.dp)
                    ) {
                        Icon(
                            tint = if (homeItems[i].isChecked) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.secondary,
                            painter = painterResource(id = homeItems[i].icon),
                            contentDescription = "",
                            modifier = Modifier
                                .weight(1f)
                                .padding(5.dp, 0.dp)
                        )
                        Text(
                            text = homeItems[i].title,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.W500),
                            color = if (homeItems[i].isChecked) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.weight(2f)
                        )
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            if (homeItems[i].showSwitch) {
                                Text(
                                    text = if (homeItems[i].isChecked) stringResource(id = R.string.android_on)
                                    else stringResource(id = R.string.android_off),
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.W600),
                                    color = if (homeItems[i].isChecked) MaterialTheme.colorScheme.onPrimaryContainer
                                    else MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                KSwitch(
                                    checked = homeItems[i].isChecked,
                                    modifier = Modifier.rotate(-90f),
                                    onClick = {
                                        homeItems[i].onCheckedChange(!homeItems[i].isChecked)
                                        onValueUpdate(i)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}