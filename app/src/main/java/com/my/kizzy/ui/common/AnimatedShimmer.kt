/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * AnimatedShimmer.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.ui.common

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedShimmer(
    content: @Composable ((Brush) -> Unit)
) {
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f),
    )

    val transition = rememberInfiniteTransition()
    val translateAnim = transition.animateFloat(
        initialValue = 0f, targetValue = 1000f, animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000, easing = FastOutSlowInEasing
            ), repeatMode = RepeatMode.Reverse
        )
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim.value, y = translateAnim.value)
    )

    content(brush)
}

@Composable
@Preview(showBackground = true)
fun ShimmerGridItemPreview() {
    AnimatedShimmer {
        ShimmerGameItems(brush = it)
    }
}


@Composable
fun ShimmerGameItems(
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
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .height(56.dp)
                .clip(RoundedCornerShape(50.dp))
                .background(brush)
        )

        repeat(6) {
            //.padding(8.dp)
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
fun ShimmerProfileCard(
    brush: Brush
) {
    Card(
        modifier = Modifier
            .padding(30.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(
                brush = brush
            ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
        )
    ) {
        Box {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(brush)
            )

            Spacer(
                modifier = Modifier
                    .padding(16.dp, 64.dp, 16.dp, 6.dp)
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(brush)
            )

            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(15.dp, 8.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                repeat(2) {
                    Spacer(
                        modifier = Modifier
                            .size(40.dp)
                            .padding(5.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(brush)
                    )
                }
            }
        }
        Column(
            Modifier
                .padding(15.dp, 5.dp, 15.dp, 15.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(brush)
        ) {
            Spacer(modifier = Modifier
                .height(40.dp)
                .padding(20.dp, 4.dp)
                .clip(RoundedCornerShape(10.dp))
                .fillMaxWidth(fraction = 0.5f)
                .background(brush))

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(19.dp, 0.dp, 19.dp, 5.dp)
                    .height(1.5.dp)
                    .background(brush)
            )
            repeat(3) {
                Spacer(modifier = Modifier
                    .height(20.dp)
                    .padding(20.dp, 4.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .fillMaxWidth(fraction = (it+1)*0.2f)
                    .background(brush))
            }
            ShimmerRpcRow(brush)
        }
    }
}

@Composable
fun ShimmerRpcRow(
     brush: Brush
) {
    Column(Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(1.dp)) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(20.dp, 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(brush),
                contentAlignment = Alignment.Center
            ) {
                Spacer(
                    Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(brush)
                )
            }
            Column {
                repeat(3) {
                    Spacer(modifier = Modifier
                        .height(23.dp)
                        .padding(20.dp, 4.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .fillMaxWidth(fraction = (it+0.62f)*0.14f)
                        .background(brush))
                }
            }
        }
    }
}
