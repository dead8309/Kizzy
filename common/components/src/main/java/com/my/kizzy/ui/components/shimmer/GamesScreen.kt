/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * GamesScreen.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.ui.components.shimmer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ShimmerGamesScreen(
    brush: Brush
) {
    Column{
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .height(70.dp)
            .clip(MaterialTheme.shapes.extraLarge)
            .background(brush))
        Spacer(modifier = Modifier.height(5.dp))

        repeat(6) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clip(RoundedCornerShape(25.dp))
                    .background(brush)
                    .padding(8.dp)
                , horizontalArrangement = Arrangement.SpaceEvenly
            ){
                Spacer(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(brush)
                )

                Column(
                    modifier = Modifier
                        .weight(9f)
                        .padding(5.dp)
                ) {
                    Spacer(
                        modifier = Modifier
                            .height(20.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .fillMaxWidth(fraction = 0.7f)
                            .background(brush)
                    )
                }
                Spacer(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(brush)
                )
            }
        }
    }
}
@Composable
@Preview(showBackground = true)
fun PreviewShimmerGamesScreen() {
    AnimatedShimmer {
        ShimmerGamesScreen(brush = it)
    }
}