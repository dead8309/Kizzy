/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * ProfileCard.kt is part of Kizzy
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

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
    Column(
        Modifier.fillMaxWidth(),
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

@Composable
@Preview(showBackground = true)
fun PreviewShimmerProfileCard() {
    AnimatedShimmer {
        ShimmerProfileCard(brush = it)
    }
}