package com.my.kizzy.ui.screen.rpc.custom

import android.app.Service
import android.content.Intent
import android.os.IBinder

class CustomRpcService : Service() {

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }
}