package com.my.kizzy.ui.screen.crash

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import com.blankj.utilcode.util.FileIOUtils
import com.blankj.utilcode.util.FileUtils
import com.my.kizzy.BuildConfig
import com.my.kizzy.R
import java.io.File
import kotlin.system.exitProcess


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrashScreen(trace: String?) {
    val ctx = LocalContext.current
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.app_crashed),
                        style = MaterialTheme.typography.headlineLarge
                            .copy(fontWeight = FontWeight.ExtraBold)
                    )
                },
                actions = {
                    IconButton(onClick = {
                        exitProcess(0)
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = Icons.Default.Close.name
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    val file = File(ctx.filesDir.toString()+"/"+ "Kizzy_Log.txt")
                    if(FileUtils.isFileExists(file)){
                        FileUtils.delete(file)
                    }
                    FileIOUtils.writeFileFromString(file,trace)
                    val uri = FileProvider.getUriForFile(ctx,"${BuildConfig.APPLICATION_ID}.provider",file)
                    val intent =
                        ShareCompat.IntentBuilder(ctx)
                            .setType("text/plain")
                            .setStream(uri)
                            .intent
                            .setAction(Intent.ACTION_SEND)
                            .setDataAndType(uri, "text/*")
                            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    ctx.startActivity(Intent.createChooser(intent, "Share File With"))
                }) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share Logs",
                    modifier = Modifier.padding(end = 5.dp)
                )
                Text(text = stringResource(id = R.string.share_crash_logs))
            }
        }
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(it))
        {
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)) {
                Text(
                    text = stringResource(id = R.string.share_crash_logs_desc),
                    style = MaterialTheme.typography.titleMedium
                        .copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 5.dp)
                )
                ElevatedCard(modifier = Modifier
                    .fillMaxSize(),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    )) {
                    LazyColumn {
                        item {
                            if (trace != null) {
                                Text(
                                    modifier = Modifier.padding(10.dp),
                                    text = trace,
                                    style = MaterialTheme.typography.bodyMedium
                                        .copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.error,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}