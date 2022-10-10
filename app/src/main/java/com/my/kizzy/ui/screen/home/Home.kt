package com.my.kizzy.ui.screen.home

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.gson.Gson
import com.my.kizzy.R
import com.my.kizzy.ui.common.Routes
import com.my.kizzy.ui.screen.profile.user.UserData
import com.my.kizzy.utils.Prefs
import com.my.kizzy.utils.Prefs.USER_DATA
import com.my.kizzy.utils.standardQuadFromTo
import com.skydoves.landscapist.glide.GlideImage
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    navController: NavController
) {
    val user: UserData? = Gson().fromJson(Prefs[USER_DATA,""])
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text =  stringResource(id = R.string.welcome)+", ${user?.name?:""}",
                        style = MaterialTheme.typography.headlineLarge,
                    )
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
                        if (user != null) {
                            GlideImage(
                                imageModel = user.avatar,
                                modifier = Modifier.size(52.dp)
                                    .border(
                                        2.dp,
                                        MaterialTheme.colorScheme.secondaryContainer,
                                        CircleShape
                                    )
                                    .clip(CircleShape),
                                previewPlaceholder = R.drawable.error_avatar)
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.Person,
                                contentDescription = Icons.Default.Person.name
                            )
                        }
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier.padding(it),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ChipSection(
                listOf(
                    Chips(
                        "Discord",
                        Intent(Intent.ACTION_VIEW, Uri.parse("https://discord.gg/vUPc7zzpV5"))
                    ),
                    Chips(
                        "Youtube",
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://youtube.com/channel/UCh-zsCv66gwHCIbMKLMJmaw")
                        )
                    )
                )
            )
            Text(
                text = stringResource(id = R.string.features),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(start = 15.dp)
            )
            RpcCards(getHomeitems(), navController)
        }
    }
}

private fun Gson.fromJson(value: String): UserData? {
   return when {
       value.isNotEmpty() -> this.fromJson(value,UserData::class.java)
       else -> null
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
                    .clip(RoundedCornerShape(12.dp))
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
                    Text(
                        text = chips[it].title,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }
            }
        }
    }
}

@Composable
fun RpcCards(
    Rpc: List<HomeItem>,
    navController: NavController
) {
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
fun RpcItem(
    item: HomeItem,
    navController: NavController
) {
    BoxWithConstraints(
        modifier = Modifier
            .padding(7.5.dp)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(25.dp),
            )
            .aspectRatio(1f)
            .clip(RoundedCornerShape(25.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .clickable { item.route?.let { navController.navigate(it) } }
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

        val color: Color = MaterialTheme.colorScheme.secondaryContainer
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
            //Middle
            drawPath(
                path = mediumColoredPath,
                color = Color(
                    manipulateColor(
                        color.toArgb(),
                        1.03f
                    )
                )
            )
            //Bottom
            drawPath(
                path = lightColoredPath,
                color = Color(
                    manipulateColor(
                        color.toArgb(),
                        0.96f
                    )
                )
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp),
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.align(Alignment.TopStart)
            )
            Icon(
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                painter = painterResource(id = item.icon),
                contentDescription = item.title,
                modifier = Modifier.align(Alignment.BottomStart)
            )
        }
    }
}

fun manipulateColor(color: Int, factor: Float): Int {
    val a = android.graphics.Color.alpha(color)
    val r = (android.graphics.Color.red(color) * factor).roundToInt()
    val g = (android.graphics.Color.green(color) * factor).roundToInt()
    val b = (android.graphics.Color.blue(color) * factor).roundToInt()
    return android.graphics.Color.argb(
        a,
        r.coerceAtMost(255),
        g.coerceAtMost(255),
        b.coerceAtMost(255)
    )
}
