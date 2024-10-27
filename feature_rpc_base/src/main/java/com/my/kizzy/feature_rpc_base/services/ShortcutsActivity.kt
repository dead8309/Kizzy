/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * ShortcutsActivity.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.feature_rpc_base.services

import android.app.Activity
import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.widget.Toast
import com.my.kizzy.resources.R

class ShortcutsActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (SDK_INT >= 27)
            setShowWhenLocked(true)
        handleShortcut()
    }

    private fun handleShortcut() {
        intent?.action?.let {
            when (it) {
                Intents.START_APP_DETECTION_SHORTCUT -> {
                    stopService(Intent(this, AppDetectionService::class.java))
                    stopService(Intent(this, MediaRpcService::class.java))
                    stopService(Intent(this, ExperimentalRpc::class.java))
                    stopService(Intent(this, SamsungRpcService::class.java))

                    startService(Intent(this, AppDetectionService::class.java))
                    Toast.makeText(this, getString(R.string.start_appDetection_toast), Toast.LENGTH_SHORT).show()
                }

                Intents.START_MEDIA_RPC_SHORTCUT -> {
                    stopService(Intent(this, AppDetectionService::class.java))
                    stopService(Intent(this, MediaRpcService::class.java))
                    stopService(Intent(this, ExperimentalRpc::class.java))
                    stopService(Intent(this, SamsungRpcService::class.java))

                    startService(Intent(this, MediaRpcService::class.java))
                    Toast.makeText(this, getString(R.string.start_mediaRPC_toast), Toast.LENGTH_SHORT).show()
                }

                Intents.START_EXPERIMENTAL_RPC_SHORTCUT -> {
                    stopService(Intent(this, AppDetectionService::class.java))
                    stopService(Intent(this, MediaRpcService::class.java))
                    stopService(Intent(this, ExperimentalRpc::class.java))
                    stopService(Intent(this, SamsungRpcService::class.java))

                    startService(Intent(this, ExperimentalRpc::class.java))
                    Toast.makeText(this, getString(R.string.start_experimentalRPC_toast), Toast.LENGTH_SHORT).show()
                }

                Intents.STOP_RPC -> {
                    stopService(Intent(this, AppDetectionService::class.java))
                    stopService(Intent(this, MediaRpcService::class.java))
                    stopService(Intent(this, ExperimentalRpc::class.java))
                    stopService(Intent(this, SamsungRpcService::class.java))

                    Toast.makeText(this, getString(R.string.stop_rpc_toast), Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }

        // Close the activity
        finish()
    }

    companion object {
        object Intents {
            val START_APP_DETECTION_SHORTCUT = "com.my.kizzy.intent.action.START_APP_DETECTION"
            val START_MEDIA_RPC_SHORTCUT = "com.my.kizzy.intent.action.START_MEDIA_RPC"
            val START_EXPERIMENTAL_RPC_SHORTCUT = "com.my.kizzy.intent.action.START_EXPERIMENTAL_RPC"
            val STOP_RPC = "com.my.kizzy.intent.action.STOP_RPC"
        }
    }
}