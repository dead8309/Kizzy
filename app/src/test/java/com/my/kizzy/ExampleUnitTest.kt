package com.my.kizzy

import com.my.kizzy.data.remote.ApiService
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    private lateinit var apiService: ApiService

    @Before
    fun setup() {
        apiService = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    @Test
    fun testApi() = runBlocking {
        val file = File("C:\\Users\\Administrator\\StudioProjects\\Kizzy\\app\\src\\main\\res\\drawable\\error_avatar.png")
       val reqBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData(
            "temp",
            file.name,
            reqBody
        )

       val response = apiService.uploadImage(part)
        if (response.isSuccessful)
            println(response.body())
    }

}