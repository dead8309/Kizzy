package com.my.kizzy.rpc

import android.content.Context
import android.graphics.Bitmap
import java.io.File


sealed class RpcImage {
    class DiscordImage(val image: String): RpcImage()
    class ExternalImage(val image: String): RpcImage()
    class ApplicationIcon(val packageName: String, val context: Context): RpcImage()
    class BitmapImage(val file: File,val bitmap: Bitmap?,val packageName: String,val title: String): RpcImage()
}
