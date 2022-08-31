package com.my.kizzy.ui.screen.home

import android.content.Intent
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.my.kizzy.R
import com.my.kizzy.ui.common.Routes
import com.my.kizzy.ui.utils.standardQuadFromTo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    navController: NavController
) {
    val user by remember {
        mutableStateOf("Kizzy")
    }
    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(text = "Hello $user")
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.navigate(Routes.SETTINGS) },
                    ) {
                        Icon(
                            Icons.Outlined.Settings,
                            Icons.Outlined.Settings.name
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Routes.PROFILE) }) {
                        Icon(imageVector = Icons.Outlined.Person,
                            contentDescription = Icons.Default.Person.name,
                            modifier = Modifier
                                .border(border = BorderStroke(
                                                    4.dp
                                                    , MaterialTheme.colorScheme.secondaryContainer),
                                        shape = CircleShape)
                                .size(48.dp)
                        )
                    }
                }
            )
        }
    ) {
        Column(modifier = Modifier.padding(it),
            verticalArrangement = Arrangement.spacedBy(16.dp)) {
            ChipSection(listOf(
                Chips(
                    "Discord",
                    R.drawable.ic_discord,
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://discord.gg/vUPc7zzpV5"))
                ),
                Chips(
                    "Youtube",
                    R.drawable.ic_youtube,
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://youtube.com/channel/UCh-zsCv66gwHCIbMKLMJmaw"))
                )
            ))
                Text(
                    text = "Features",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(start = 10.dp)
                )
                RpcCards(getHomeitems(), navController)
        }
    }
}

@Composable
fun ChipSection(
    chips: List<Chips>
) {

    val context = LocalContext.current
    LazyRow {
        items(chips.size) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(start = 15.dp, top = 15.dp, bottom = 15.dp)
                    .clickable {
                        context.startActivity(chips[it].intent)
                    }
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        MaterialTheme.colorScheme.secondaryContainer
                    )
                    .padding(15.dp)
            )
            {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                {
                    Image(
                        painterResource(chips[it].icon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                    )
                    Text(text = chips[it].title,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(start = 5.dp)
                    )

                }
            }
        }
    }
}

@Composable
fun RpcCards(Rpc: List<HomeItem>,
navController: NavController) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(start = 7.5.dp, end = 7.5.dp, bottom = 7.5.dp),
        modifier = Modifier.fillMaxHeight()
    )
    {
        items(Rpc.size) {
            RpcItem(
                item = Rpc[it],
                navController
            )
        }
    }
}

@Composable
fun RpcItem(item: HomeItem,
            navController: NavController
) {
    BoxWithConstraints(
        modifier = Modifier
            .padding(16.dp)
            .height(200.dp)
            .width(180.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(item.bgColor)
            .clickable { navController.navigate(item.route) }
    ) {
        val width = constraints.maxWidth
        val height = constraints.maxHeight

        // Medium colored path
        val mediumColoredPoint1 = Offset(0f, height * 0.3f)
        val mediumColoredPoint2 = Offset(width * 0.1f, height * 0.35f)
        val mediumColoredPoint3 = Offset(width * 0.4f, height * 0.05f)
        val mediumColoredPoint4 = Offset(width * 0.75f, height * 0.7f)
        val mediumColoredPoint5 = Offset(width * 1.4f, -height.toFloat())

        val mediumColoredPath = Path().apply {
            moveTo(mediumColoredPoint1.x, mediumColoredPoint1.y)
            standardQuadFromTo(mediumColoredPoint1, mediumColoredPoint2)
            standardQuadFromTo(mediumColoredPoint2, mediumColoredPoint3)
            standardQuadFromTo(mediumColoredPoint3, mediumColoredPoint4)
            standardQuadFromTo(mediumColoredPoint4, mediumColoredPoint5)
            lineTo(width.toFloat() + 100f, height.toFloat() + 100f)
            lineTo(-100f, height.toFloat() + 100f)
            close()
        }

        // Light colored path
        val lightPoint1 = Offset(0f, height * 0.35f)
        val lightPoint2 = Offset(width * 0.1f, height * 0.4f)
        val lightPoint3 = Offset(width * 0.3f, height * 0.35f)
        val lightPoint4 = Offset(width * 0.65f, height.toFloat())
        val lightPoint5 = Offset(width * 1.4f, -height.toFloat() / 3f)

        val lightColoredPath = Path().apply {
            moveTo(lightPoint1.x, lightPoint1.y)
            standardQuadFromTo(lightPoint1, lightPoint2)
            standardQuadFromTo(lightPoint2, lightPoint3)
            standardQuadFromTo(lightPoint3, lightPoint4)
            standardQuadFromTo(lightPoint4, lightPoint5)
            lineTo(width.toFloat() + 100f, height.toFloat() + 100f)
            lineTo(-100f, height.toFloat() + 100f)
            close()
        }
        Canvas(
            modifier = Modifier
                .fillMaxSize()
        ) {
            drawPath(
                path = mediumColoredPath,
                color = item.mediumColor
            )
            drawPath(
                path = lightColoredPath,
                color = item.lightColor
            )
        }
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween) {
            Icon(
                tint = item.iconColor,
                painter = painterResource(id = item.icon),
                contentDescription = item.title
            )
            Text(
                text = item.title,
                style = TextStyle(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.W600,
                    color = Color.Black
                )
            )
        }
    }
}

@Preview(showBackground = true,
uiMode = UI_MODE_NIGHT_YES)
@Composable
fun Prev(){
    val navController = rememberNavController()
    Home(navController = navController)
}
