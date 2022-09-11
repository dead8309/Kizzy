package com.my.kizzy.rpc

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.DisplayMetrics
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.my.kizzy.BuildConfig
import com.my.kizzy.utils.Prefs
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.CountDownLatch

@Suppress("DEPRECATION")
class ImageResolver() {

    /**
     * This function uploads Application image to a Discord channel and retrieve its snowflake ids
     * @return SnowFlake id of image or null
     * @param packageName String
     * @param context Context
     */
    fun resolveImageOfAppIcon(packageName: String,context: Context): String? {
        val data = Prefs[Prefs.SAVED_IMAGES,"{}"]
        val savedImages =  Gson().fromJson<HashMap<String, String>>(data,
            object : TypeToken<HashMap<String, String>>() {}.type)
        return if (savedImages.containsKey(packageName))
            savedImages[packageName]
        else
            retrieveImageFromHook(packageName,savedImages,context)
    }

    /**
     * This function uploads external image from specified url to a Discord channel and retrieve its snowflake id
     * @return SnowFlake id of image or null
     * @param large_image String
     */
    fun resolveImageFromUrl(large_image: String): String? {
        return null
    }


    private fun retrieveImageFromHook(
        packageName: String,
        saved_images: HashMap<String, String>,
        context: Context
    ): String? {

        val applicationInfo =
            context.packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        val res = context.packageManager.getResourcesForApplication(applicationInfo)
        val icon = res.getDrawableForDensity(
            applicationInfo.icon,
            DisplayMetrics.DENSITY_XXXHIGH
        )
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
            }
        }
        if (icon != null) {
            if (canvas != null) {
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


    private fun uploadImage(file: File): String? {
            var result: String? = null
            val client = OkHttpClient()
            val body: RequestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "file", file.name,
                    file.asRequestBody("image/png".toMediaTypeOrNull())
                )
                .addFormDataPart("content", "${file.name} from Rpc")
                .build()

            val req: Request = Request.Builder()
                .url(BuildConfig.RPC_IMAGE_API)
                .post(body)
                .build()

            val cd = CountDownLatch(1)
            client.newCall(req).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    result = null
                    cd.countDown()
                }

                @Suppress("UNCHECKED_CAST")
                override fun onResponse(call: Call, response: Response) {
                    val res: String = response.body!!.string()
                    val type = object : TypeToken<Map<String, Any>>() {}.type
                    val data = Gson().fromJson<Map<String, Any>>(res, type)
                    val image: ArrayList<Map<String, Any>> =
                        data["attachments"] as ArrayList<Map<String, Any>>
                    result = image[0]["proxy_url"] as String
                    result = "mp:" + result!!.substring(result!!.indexOf("attachments/"))
                    cd.countDown()
                }
            })
            cd.await()
            return result
        }

        private fun saveIcon(
            dir: File,
            bitmap: Bitmap?,
            name: String = "Temp.png",
            format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG,
            quality: Int = 100,
        ): File {

            val image = File(dir, name)
            val fos = FileOutputStream(image)
            bitmap?.compress(format, quality, fos)
            fos.close()
            return image
        }

    }
