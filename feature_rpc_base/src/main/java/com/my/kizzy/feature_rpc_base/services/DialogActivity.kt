/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * DialogActivity.kt is part of Kizzy
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
import androidx.appcompat.view.ContextThemeWrapper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.my.kizzy.resources.R

class DialogActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (SDK_INT >= 27)
            setShowWhenLocked(true)
        showDialog()
    }

    private fun showDialog() {
        val rpc = arrayOf(
            getString(R.string.main_appDetection),
            getString(R.string.main_mediaRpc),
            getString(R.string.main_experimentalRpc),
            getString(R.string.main_samsungRpc)
        )
        val dialog = MaterialAlertDialogBuilder(ContextThemeWrapper(this, com.my.kizzy.feature_rpc_base.R.style.MyTileDialogTheme))
            .setTitle(getString(R.string.choose_rpc))
            .setSingleChoiceItems(rpc, -1) { dialog, which ->
                when (which) {
                    0 -> startService(Intent(this, AppDetectionService::class.java))
                    1 -> startService(Intent(this, MediaRpcService::class.java))
                    2 -> startService(Intent(this, ExperimentalRpc::class.java))
                    3 -> startService(Intent(this, SamsungRpcService::class.java))
                }
                dialog.dismiss()
                finish()
            }
            .create()
        dialog.setOnDismissListener { finish() }
        dialog.show()
    }
}