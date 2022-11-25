/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * UserState.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.ui.screen.profile.user

import com.my.kizzy.data.remote.User

data class UserState(
val user: User? = null,
val error: String = "",
val loading: Boolean = false
)