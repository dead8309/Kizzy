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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.nestedscroll.nestedScroll


import com.my.kizzy.ui.common.PreferenceSwitch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppsRPC(navController: NavHostController) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(),
        canScroll = { true })
        
    Scaffold(
    modifier = Modifier
        .fillMaxSize()
        .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "Apps",
                        style = MaterialTheme.typography.headlineLarge,
                    )
                },
                navigationIcon = { BackButton{navController.popBackStack()} },
                scrollBehavior = scrollBehavior
            )
        }
    ){
    
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(it)) {
            
            var serviceEnabled by remember {
                        mutableStateOf(false)
                    }
                    
            LazyColumn{
                item {
                    SwitchBar(
                title = "Enable App Detection",
                checked = serviceEnabled,
                onClick = { serviceEnabled = !serviceEnabled}
                )
                }
                for (item in getItems()){
                    item {
                        var isChecked by remember {
                        mutableStateOf(true)
                        }
                        PreferenceSwitch(
                        title = item,
                        description = "packageName",
                        isChecked = isChecked,
                        onClick = { isChecked = !isChecked }
                        )
                    }
                }
            }
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
        "Hello","hi","Hello","hi","Hello","hi","Hello","hi","Hello","hi",
        "Hello","hi","Hello","hi","Hello","hi","Hello","hi","Hello","hi",
         "Hello","hi","Hello","hi","Hello","hi","Hello","hi","Hello","hi",
        "Hello","hi","Hello","hi","Hello","hi","Hello","hi","Hello","hi",
        "Hello","hi","Hello","hi","Hello","hi","Hello","hi","Hello","hi",
         "Hello","hi","Hello","hi","Hello","hi","Hello","hi","Hello","hi",
        "Hello","hi","Hello","hi","Hello","hi","Hello","hi","Hello","hi",
        "Hello","hi","Hello","hi","Hello","hi","Hello","hi","Hello","hi",
)

