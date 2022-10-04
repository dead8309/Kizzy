package com.my.kizzy.ui.screen.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.my.kizzy.R
import com.my.kizzy.ui.screen.profile.user.UserData
import com.my.kizzy.ui.theme.DISCORD_DARK
import com.my.kizzy.ui.theme.DISCORD_GREY
import com.my.kizzy.ui.theme.DarkBlueBg
import com.my.kizzy.utils.Prefs
import com.my.kizzy.utils.Prefs.USER_DATA
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.delay

@Preview
@Composable
fun UserPreview() {
    User()
}

@Composable
fun User() {
    var elapsed by remember {
        mutableStateOf(0)
    }
    LaunchedEffect(elapsed) {
        if (elapsed == 60)
            elapsed = 0
        else {
            delay(1000)
            elapsed++
        }
    }
    Card(
        elevation = CardDefaults.cardElevation(12.dp),
        modifier = Modifier
            .padding(30.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = DISCORD_DARK,
        ),
    ) {
        val user = Gson().fromJson(Prefs[USER_DATA, "{}"],UserData::class.java)
        Box {
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(65.dp)
                .background(DISCORD_GREY))
            GlideImage(
                imageModel = user.avatar,
                placeHolder = ImageBitmap.imageResource(id = R.drawable.error_avatar),
                error = ImageBitmap.imageResource(id = R.drawable.error_avatar),
                contentDescription = null,
                modifier = Modifier
                    .padding(16.dp, 16.dp, 16.dp, 6.dp)
                    .size(110.dp)
                    .border(width = 8.dp,
                        color = DISCORD_DARK,
                        shape = CircleShape)
                    .clip(CircleShape)
            )
        }
        CardText(
            text = user.username,
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .padding(19.dp, 0.dp, 19.dp, 5.dp)
            .height(1.5.dp)
            .background(DISCORD_GREY)
        )
        CardText(
            text = "ABOUT ME",
            style = MaterialTheme.typography.titleSmall
        )
        CardText(text = user.about,
            style = MaterialTheme.typography.bodyMedium,
            bold = false)
        CardText(
            text = "USING KIZZY RICH PRESENCE",
            style = MaterialTheme.typography.titleSmall
        )
        Row(Modifier
            .fillMaxWidth()
            .height(90.dp)
            .padding(20.dp, 4.dp)) {

            Box(modifier = Modifier
                .size(70.dp)
                .clip(RoundedCornerShape(15.dp))
                .background(DarkBlueBg),
                contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(id = R.drawable.editing_rpc_pencil),
                    contentDescription = "rpc_pencil",
                    modifier = Modifier
                        .size(48.dp)
                        .background(DarkBlueBg, RoundedCornerShape(15.dp))
                )
            }
            Column {
                CardText(
                    text = "User Profile",
                    style = MaterialTheme.typography.titleSmall
                        .copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 16.sp)
                )
                CardText(
                    text = "00:$elapsed",
                    style = MaterialTheme.typography.titleSmall
                        .copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 16.sp)
                )
            }
        }
    }
}


@Composable
fun CardText(
    text: String,
    style: TextStyle,
    bold: Boolean = true,
) {
    Text(
        text = text,
        style = if (!bold) style
        else style.copy(fontWeight = FontWeight.ExtraBold),
        modifier = Modifier.padding(20.dp, 4.dp),
        color = Color.White.copy(alpha = 0.8f)
    )
}