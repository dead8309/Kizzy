package com.my.kizzy.ui.screen.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.my.kizzy.ui.common.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    navController: NavController
) {
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(text = "Kizzy")
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.navigate(Routes.SETTINGS) },
                    ) {
                        Icon(
                            Icons.Default.Settings,
                            Icons.Default.Settings.name
                        )
                    }
                }
            )
        }
    ) {
        Column(modifier = Modifier.padding(it),
            verticalArrangement = Arrangement.spacedBy(16.dp)) {
            RpcCards(getHomeitems(),navController)
        }
    }
}

@Composable
fun RpcCards(Rpc: List<HomeItem>,
navController: NavController) {
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
                    .height(200.dp)
                    .width(180.dp)
                    .clickable { navController.navigate(Rpcs.route) }
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
