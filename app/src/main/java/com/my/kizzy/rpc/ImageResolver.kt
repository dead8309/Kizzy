package com.my.kizzy.rpc

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.DisplayMetrics
import com.blankj.utilcode.util.AppUtils
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.my.kizzy.BuildConfig
import com.my.kizzy.utils.Prefs
import com.my.kizzy.utils.Prefs.RPC_USE_LOW_RES_ICON
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.CountDownLatch

@Suppress("DEPRECATION")
class ImageResolver {

    /**
     * This function uploads Application image to a Discord channel and retrieve its snowflake ids
     * @return SnowFlake id of image or null
     * @param packageName String
     * @param context Context
     */
    fun resolveImageOfAppIcon(packageName: String, context: Context): String? {
        val data = Prefs[Prefs.SAVED_IMAGES, "{}"]
        val savedImages = Gson().fromJson<HashMap<String, String>>(data,
            object : TypeToken<HashMap<String, String>>() {}.type)
        return if (savedImages.containsKey(packageName))
            savedImages[packageName]
        else
            retrieveImageFromHook(packageName, savedImages, context)
    }

    /**
     * This function uploads external image from specified url to a Discord channel and retrieve its snowflake id
     * @return SnowFlake id of image or null
     * @param large_image_url String
     */
    fun resolveImageFromUrl(large_image_url: String): String? {
        var result: String? = null
        val client = OkHttpClient()
        val request: Request = Request.Builder()
            .url("${BuildConfig.BASE_URL}/image?url=$large_image_url")
            .build()
        val countDownLatch = CountDownLatch(1)
        client.newCall(request)
            .enqueue(
                object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        e.printStackTrace()
                        result = "mp:attachments/948828217312178227/948840504542498826/Kizzy.png"
                        countDownLatch.countDown()
                    }

                    @Throws(IOException::class)
                    override fun onResponse(call: Call, response: Response) {
                        if (!response.isSuccessful) return
                        val responseBody: ResponseBody = response.body!!
                        result = try {
                                Gson().fromJson(
                                    responseBody.string(),
                                    Result::class.java
                                ).id
                        } catch (e: JsonSyntaxException) {
                            "mp:attachments/948828217312178227/948840504542498826/Kizzy.png"
                        }
                        countDownLatch.countDown()
                    }
                })
        try {
            countDownLatch.await()
        } catch (ignored: InterruptedException) {
        }
        return result
    }


    private fun retrieveImageFromHook(
        packageName: String,
        saved_images: HashMap<String, String>,
        context: Context,
    ): String? {

        val applicationInfo =
            context.packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        val res = context.packageManager.getResourcesForApplication(applicationInfo)
        val icon = if (Prefs[RPC_USE_LOW_RES_ICON, false])
            AppUtils.getAppIcon(packageName)
        else res.getDrawableForDensity(
            applicationInfo.icon,
            DisplayMetrics.DENSITY_XXXHIGH)

        val bitmap = icon?.let {
            Bitmap.createBitmap(it.intrinsicWidth,
                icon.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
        }
        val canvas = bitmap?.let { Canvas(it) }
        if (icon != null) {
            if (canvas != null) {
                icon.setBounds(0, 0, canvas.width, canvas.height)
                icon.draw(canvas)
            }
        }
        val file = File(context.filesDir.toString() + File.separator + "image")
        if (!file.exists())
            file.mkdirs()

        val response: String? = uploadImage(saveIcon(file, bitmap))
        saved_images[packageName]
        response?.let {
            saved_images[packageName] = it
            Prefs[Prefs.SAVED_IMAGES] = Gson().toJson(saved_images)
        }
        return response
    }


     fun uploadImage(file: File): String? {
        var result: String? = null
        val client = OkHttpClient()
        val body: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "temp", file.name,
                file.asRequestBody("image/png".toMediaTypeOrNull())
            )
            .build()

        val req: Request = Request.Builder()
            .url("${BuildConfig.BASE_URL}/upload")
            .post(body)
            .build()

        val cd = CountDownLatch(1)
        client.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                result = null
                cd.countDown()
            }

            override fun onResponse(call: Call, response: Response) {
                result = try{
                    Gson().fromJson(response.body!!.string(),Result::class.java).id
                } catch (e: Exception){
                    "mp:attachments/948828217312178227/948840504542498826/Kizzy.png"
                }
                cd.countDown()
            }
        })
        cd.await()
        return result
    }

     fun saveIcon(
        dir: File,
        bitmap: Bitmap?,
        name: String = "Temp.png",
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG,
        quality: Int = 100,
    ): File {
         dir.mkdir()
        val image = File(dir, name)
        val fos = FileOutputStream(image)
        bitmap?.compress(format, quality, fos)
        fos.close()
        return image
    }
}

data class Result(
    @SerializedName("id")
    val id: String
)