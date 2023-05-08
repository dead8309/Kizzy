/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * RpcServiceUtils.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.feature_rpc_base

import android.content.Context
import android.content.Intent
import com.my.kizzy.feature_rpc_base.services.AppDetectionService
import com.my.kizzy.feature_rpc_base.services.CustomRpcService
import com.my.kizzy.feature_rpc_base.services.ExperimentalRpc
import com.my.kizzy.feature_rpc_base.services.MediaRpcService

val rpcServices = listOf(
    AppDetectionService::class.java,
    MediaRpcService::class.java,
    CustomRpcService::class.java,
    ExperimentalRpc::class.java
)

inline fun <reified T : Any> Context.startServiceAndStopOthers() {
    rpcServices.forEach {
        if (it == T::class.java) {
            startService<T>()
        } else {
            stopService<T>()
        }
    }
}

inline fun <reified T : Any> Context.startService() {
    this.startService(Intent(this, T::class.java))
}

inline fun <reified T : Any> Context.stopService() {
    this.stopService(Intent(this, T::class.java))
}

