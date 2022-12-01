package com.my.kizzy.ui.screen.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

data class HomeItem(
    val title: String,
    @DrawableRes val icon: Int,
    val route: String? = null,
    val isChecked: Boolean = false,
    val onClick: (String) -> Unit = {},
    val onCheckedChange: (Boolean) -> Unit = {},
    val showSwitch: Boolean = true,
    val shape: RoundedCornerShape = RoundedCornerShape(0.dp)
)