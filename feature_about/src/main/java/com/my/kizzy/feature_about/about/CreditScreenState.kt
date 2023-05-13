/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * CreditScreenState.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.feature_about.about

import com.my.kizzy.domain.model.Contributor

sealed interface CreditScreenState {
    object Loading: CreditScreenState
    class Error(val error: String?): CreditScreenState
    class LoadingCompleted(val contributors: List<Contributor>): CreditScreenState
}