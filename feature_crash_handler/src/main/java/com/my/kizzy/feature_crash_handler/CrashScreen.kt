/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * CrashScreen.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.feature_crash_handler

import android.content.Intent
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import com.blankj.utilcode.util.FileIOUtils
import com.blankj.utilcode.util.FileUtils
import com.my.kizzy.resources.R
import java.io.File
import kotlin.system.exitProcess

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrashScreen(trace: String?) {
    val ctx = LocalContext.current
    Scaffold(topBar = {
        TopAppBar(title = {
            Text(
                text = stringResource(id = R.string.app_crashed),
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold)
            )
        }, actions = {
            IconButton(onClick = {
                exitProcess(0)
            }) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = Icons.Default.Close.name
                )
            }
        })
    }, floatingActionButton = {
        ExtendedFloatingActionButton(onClick = {
            val file = File(ctx.filesDir.toString() + "/" + "Kizzy_Log.txt")
            if (FileUtils.isFileExists(file)) {
                FileUtils.delete(file)
            }
            FileIOUtils.writeFileFromString(file, trace)
            val uri = FileProvider.getUriForFile(
                ctx,
                "com.my.kizzy.provider",
                file
            )
            val intent = ShareCompat.IntentBuilder(ctx).setType("text/plain")
                .setStream(uri).intent.setAction(Intent.ACTION_SEND).setDataAndType(uri, "text/*")
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
    }) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.share_crash_logs_desc),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 5.dp)
                )
                ElevatedCard(
                    modifier = Modifier.fillMaxSize(), colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    )
                ) {
                    LazyColumn {
                        item {
                            if (trace != null) {
                                Text(
                                    modifier = Modifier.padding(10.dp),
                                    text = trace,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
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

// This error is thrown when notification listener permission is not granted
@Preview
@Composable
fun PreviewCrashScreen() = CrashScreen(trace = """
    Kizzy crash report
    Manufacturer: ******
    Device: *****
    Android version: **
    App version: *** (**)
    Stacktrace: 
    java.lang.SecurityException: Missing permission to control media.
    	at android.os.Parcel.createExceptionOrNull(Parcel.java:3011)
    	at android.os.Parcel.createException(Parcel.java:2995)
    	at android.os.Parcel.readException(Parcel.java:2978)
    	at android.os.Parcel.readException(Parcel.java:2920)
    	at android.media.session.ISessionManager$\Stub$\Proxy.getSessions(ISessionManager.java:672)
    	at android.media.session.MediaSessionManager.getActiveSessionsForUser(MediaSessionManager.java:272)
    	at android.media.session.MediaSessionManager.getActiveSessions(MediaSessionManager.java:194)
    	at com.my.kizzy.data.get_current_data.media.GetCurrentPlayingMedia.invoke(GetCurrentlyPlayingMedia.kt:38)
    	at com.my.kizzy.services.MediaRpcService$\onCreate${'$'}1.invokeSuspend(MediaRpcService.kt:61)
    	at kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:33)
    	at kotlinx.coroutines.DispatchedTask.run(DispatchedTask.kt:106)
    	at kotlinx.coroutines.internal.LimitedDispatcher.run(LimitedDispatcher.kt:42)
    	at kotlinx.coroutines.scheduling.TaskImpl.run(Tasks.kt:95)
    	at kotlinx.coroutines.scheduling.CoroutineScheduler.runSafely(CoroutineScheduler.kt:570)
    	at kotlinx.coroutines.scheduling.CoroutineScheduler$\Worker.executeTask(CoroutineScheduler.kt:750)
    	at kotlinx.coroutines.scheduling.CoroutineScheduler$\Worker.runWorker(CoroutineScheduler.kt:677)
    	at kotlinx.coroutines.scheduling.CoroutineScheduler$\Worker.run(CoroutineScheduler.kt:664)
    	Suppressed: kotlinx.coroutines.DiagnosticCoroutineContextException: [StandaloneCoroutine{Cancelling}@2fd5814, Dispatchers.IO]
    Caused by: android.os.RemoteException: Remote stack trace:
    	at com.android.server.media.MediaSessionService.enforceMediaPermissions(MediaSessionService.java:616)
    	at com.android.server.media.MediaSessionService.-${'$'}$\Nest$\menforceMediaPermissions(Unknown Source:0)
    	at com.android.server.media.MediaSessionService$\SessionManagerImpl.verifySessionsRequest(MediaSessionService.java:2163)
    	at com.android.server.media.MediaSessionService$\SessionManagerImpl.getSessions(MediaSessionService.java:1269)
    	at android.media.session.ISessionManager$\Stub.onTransact(ISessionManager.java:317)


""".trimIndent())