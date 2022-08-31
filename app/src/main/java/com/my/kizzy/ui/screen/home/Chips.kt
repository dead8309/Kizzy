package com.my.kizzy.ui.screen.home

import android.content.Intent
import androidx.annotation.DrawableRes

data class Chips(
    val title: String,
    @DrawableRes val icon: Int,
    val intent: Intent
)

