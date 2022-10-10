package com.my.kizzy.rpc

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
                cd.countDown()
            }

            override fun onResponse(call: Call, response: Response) {
                assert(response.isSuccessful)
                cd.countDown()
            }
        })
        cd.await()
    }
}