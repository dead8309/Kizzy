package com.my.kizzy.service

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import com.google.gson.Gson
import com.my.kizzy.ui.screen.custom.Rpc

class CustomRpcService : Service() {
    lateinit var rpc: Rpc

    @Suppress("DEPRECATION")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val string =
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU)
                intent?.getStringExtra("RPC")
        else
            intent?.getStringExtra("RPC")

        rpc = Gson().fromJson(string, Rpc::class.java)
        return super.onStartCommand(intent, flags, startId)
    }


    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }


}