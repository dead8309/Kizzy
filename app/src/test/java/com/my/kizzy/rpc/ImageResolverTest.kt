package com.my.kizzy.rpc

import com.google.gson.Gson
import com.my.kizzy.BuildConfig
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import org.junit.Test
import java.io.File
import java.io.IOException
import java.util.concurrent.CountDownLatch

class ImageResolverTest {



    @Test
    fun upload() {
       val file = File("C:\\Users\\Administrator\\StudioProjects\\Kizzy\\app\\src\\main\\res\\mipmap-hdpi\\ic_launcher.png")
        val client = OkHttpClient()
        val body: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "temp", file.name,
                file.asRequestBody("image/png".toMediaTypeOrNull())
            )
            .build()
        val request: Request = Request.Builder()
            .url("${BuildConfig.BASE_URL}/image?url=https://media.discordapp.net/attachments/1030247070717714483/1030248582558777404/Temp.png")
            .build()


        val req: Request = Request.Builder()
            .url(BuildConfig.BASE_URL+"/upload")
            .post(body)
            .build()

        val cd = CountDownLatch(1)
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                cd.countDown()
            }

            override fun onResponse(call: Call, response: Response) {
                println(
                    Gson().fromJson(response.body?.string(),Result::class.java)
                )
                assert(response.isSuccessful)
                cd.countDown()
            }
        })
        cd.await()
    }
}