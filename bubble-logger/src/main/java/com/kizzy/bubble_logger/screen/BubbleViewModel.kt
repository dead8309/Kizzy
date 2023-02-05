package com.kizzy.bubble_logger.screen

import androidx.lifecycle.ViewModel
import com.kizzy.bubble_logger.BubbleDataHelper

internal class BubbleViewModel : ViewModel() {

    val logs = BubbleDataHelper.logs
}