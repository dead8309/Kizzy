/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * CommonRpc.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.data.rpc

import kizzy.gateway.entities.presence.Timestamps

data class CommonRpc(
    val name: String = "",
    val details: String? = "",
    val state: String? = "",
    val largeImage: RpcImage? = null,
    val smallImage: RpcImage? = null,
    val time: Timestamps? = null,
    val packageName: String = ""
)
