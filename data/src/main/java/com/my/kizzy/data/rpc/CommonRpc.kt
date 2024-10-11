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

data class CommonRpc(
    val name: String = "",
    val details: String? = "",
    val state: String? = "",
    val partyCurrentSize: Int? = null,
    val partyMaxSize: Int? = null,
    val largeImage: RpcImage? = null,
    val smallImage: RpcImage? = null,
    var largeText: String? = "",
    var smallText: String? = "",
    val time: Timestamps? = null,
    val packageName: String = ""
)
