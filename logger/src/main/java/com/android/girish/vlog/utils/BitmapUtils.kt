package com.android.girish.vlog.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import java.io.IOException
import java.net.HttpURLConnection

fun Bitmap.addBackground(color: Int): Bitmap {
    val newBitmap = Bitmap.createBitmap(width, height, config)
    val canvas = Canvas(newBitmap)
    canvas.drawColor(color)
    val rect = Rect(0, 0, width, height)
    canvas.drawBitmap(this, rect, rect, null)
    return newBitmap
}

fun Bitmap.makeCircular(): Bitmap {
    val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(output)

    val paint = Paint()
    val rect = Rect(0, 0, width, height)

    paint.isAntiAlias = true
    canvas.drawARGB(0, 0, 0, 0)

    paint.color = Color.WHITE
    paint.style = Paint.Style.FILL
    canvas.drawCircle(output.width.toFloat() / 2, output.height.toFloat() / 2, output.width.toFloat() / 2, paint)

    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(this, rect, rect, paint)

    return output
}

fun Bitmap.addShadow(): Bitmap {
    val bmOut = Bitmap.createBitmap(width + 10, height + 20, Bitmap.Config.ARGB_8888)

    val centerX = (bmOut.width / 2 - width / 2).toFloat()
    val centerY = (bmOut.height / 2 - height / 2).toFloat()

    val canvas = Canvas(bmOut)
    canvas.drawColor(0, PorterDuff.Mode.CLEAR)
    val ptBlur = Paint()
    ptBlur.maskFilter = BlurMaskFilter(6f, BlurMaskFilter.Blur.NORMAL)
    val offsetXY = IntArray(2)
    val bmAlpha = extractAlpha(ptBlur, offsetXY)
    val ptAlphaColor = Paint()
    ptAlphaColor.color = Color.argb(80, 0, 0, 0)
    canvas.drawBitmap(bmAlpha, centerX + offsetXY[0], centerY + offsetXY[1] + 4f, ptAlphaColor)
    bmAlpha.recycle()
    canvas.drawBitmap(this, centerX, centerY, null)
    return bmOut
}

fun Bitmap.scaleToSize(size: Int): Bitmap {
    return Bitmap.createScaledBitmap(this, size, size, true)
}

fun fetchBitmap(urlStr: String): Bitmap? {
    try {
        val url = java.net.URL(urlStr)
        val connection = url
            .openConnection() as HttpURLConnection
        connection.doInput = true
        connection.connect()
        val input = connection.inputStream
        return BitmapFactory.decodeStream(input)
    } catch (e: IOException) {
        e.printStackTrace()
        return null
    }
}

fun drawableToBitmap(drawable: Drawable): Bitmap {
    if (drawable is BitmapDrawable) {
        return drawable.bitmap
    }

    // We ask for the bounds if they have been set as they would be most
    // correct, then we check we are  > 0
    val width = if (!drawable.bounds.isEmpty) drawable.bounds.width() else drawable.intrinsicWidth
    val height = if (!drawable.bounds.isEmpty) drawable.bounds.height() else drawable.intrinsicHeight

    // Now we check we are > 0
    val bitmap = Bitmap.createBitmap(
        if (width <= 0) 1 else width,
        if (height <= 0) 1 else height,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}
