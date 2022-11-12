package com.my.kizzy

import androidx.collection.ArrayMap
import com.google.gson.Gson
import com.my.kizzy.data.remote.ApiService
import com.my.kizzy.rpc.Constants
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
    @Test
    fun testJson(){
        val rpc = ArrayMap<String, Any>()
        val buttons = ArrayList<String>()
        val buttonUrl = ArrayList<String>()
        buttons.add("Button1")
        buttons.add("Button2")
        buttonUrl.add("Button1 Url")
        buttonUrl.add("Button2 Url")
        val presence = ArrayMap<String, Any?>()
        val activity = ArrayMap<String, Any?>()
        activity[Constants.NAME] = "activityName"
        activity[Constants.DETAILS] = "details"
        activity[Constants.STATE] = "state"
        activity[Constants.TYPE] = 0
        val timestamps = ArrayMap<String, Long?>()
        timestamps[Constants.START_TIMESTAMPS] = 4999889888L
        timestamps[Constants.STOP_TIMESTAMPS] = 49998898988888L
        activity[Constants.TIMESTAMPS] = timestamps
        val assets = ArrayMap<String, String?>()
        assets[Constants.LARGE_IMAGE] = "largeImage?.resolveImage(kizzyRepository)"
        assets[Constants.SMALL_IMAGE] = "smallImage?.resolveImage(kizzyRepository)"
        activity[Constants.ASSETS] = assets

            activity[Constants.APPLICATION] = Constants.APPLICATION_ID
            activity[Constants.BUTTONS] = buttons
            val metadata = ArrayMap<String, Any>()
            metadata[Constants.BUTTON_LINK] = buttonUrl
            activity[Constants.METADATA] = metadata

        presence[Constants.ACTIVITIES] = arrayOf<Any>(activity)
        presence[Constants.AFK] = true
        presence[Constants.SINCE] = 4999889888L
        presence[Constants.STATUS] = "status"
        rpc["op"] = 3
        rpc["d"] = presence
        println(Gson().toJson(rpc))
    }
}