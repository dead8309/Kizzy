package com.my.kizzy.ui.screen.rpc.apps

import android.icu.text.CaseMap
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessAlarm
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.my.kizzy.ui.common.BackButton
import com.my.kizzy.ui.common.Routes
import com.my.kizzy.ui.common.SwitchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppsRPC(navController: NavHostController) {
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "Apps",
                        style = MaterialTheme.typography.headlineLarge,
                    )
                },
                navigationIcon = { BackButton{navController.popBackStack()} },
                scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
            )
        }
    ){
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(it)) {
            SwitchBar(
                title = "Enable App Detection",
                checked = false,
                onClick = {}
            )

            LazyColumn{
                for (item in getItems()){
                    item {
                        ItemCard(
                            title = item,
                            subtitile = "package name")
                    }
                }
            }
        }
    }
}

@Composable
fun ItemCard(title: String,subtitile: String) {
    Card(shape = RoundedCornerShape(15.dp),
    modifier = Modifier
        .padding(15.dp)
        .fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.Android,
                contentDescription = null
            )
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            ){
                Text(
                    text = subtitile,
                    style = MaterialTheme
                        .typography
                        .bodySmall
                        .copy(
                        fontStyle = FontStyle.Italic
                        ),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(bottom = 25.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                )
            }
            Switch(
                checked = false,
                onCheckedChange = {}
            )
        }
    }

}


@Preview
@Composable
fun AppsSreen() {
    val navController = rememberNavController()
    AppsRPC(navController = navController)
}

fun getItems():List<String> = listOf(
        "Hello","hi","Hello","hi","Hello","hi","Hello","hi","Hello","hi",
)