package com.my.kizzy.ui.theme.screen.home

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Home() {
    RpcCards(getHomeitems())
}

@Composable
fun RpcCards(Rpc: List<HomeItem>) {
    val scrollState = rememberScrollState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(
                state = scrollState,
            )
    ) {
        for (Rpcs in Rpc) {

            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSystemInDarkTheme()) Rpcs.bgColor[0] else Rpcs.bgColor[1]
                ),
                modifier = Modifier
                    .padding(16.dp)
                    .height(220.dp)
                    .width(180.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(10.dp),
                    verticalArrangement = Arrangement.SpaceBetween) {
                    Icon(
                        tint = if (isSystemInDarkTheme()) Rpcs.iconColor[0] else Rpcs.iconColor[1],
                        painter = painterResource(id = Rpcs.icon),
                        contentDescription = Rpcs.title
                    )
                    Text(
                        text = Rpcs.title,
                        style = TextStyle(
                            fontSize = 25.sp,
                            fontWeight = FontWeight.W700
                        ),
                        color = if (isSystemInDarkTheme()) Color.White else Color.Black
                    )
                }
            }
        }
    }
}
