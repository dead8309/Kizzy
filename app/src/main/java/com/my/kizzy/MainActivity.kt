package com.my.kizzy

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.my.kizzy.ui.theme.KizzyTheme
import com.my.kizzy.ui.theme.screen.home.Home
import me.rerere.md3compat.Md3CompatTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Md3CompatTheme {
                Kizzy()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Kizzy() {
        Scaffold(
            topBar = {
                LargeTopAppBar(
                    title = {
                        Text(text = "Kizzy")
                    }
                )
            }
        ) {
            Column(modifier = Modifier.padding(it),
                verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Home()
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        MaterialTheme {
            Kizzy()
        }
    }
}