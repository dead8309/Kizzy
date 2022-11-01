package com.my.kizzy.data.remote
import com.google.gson.annotations.SerializedName


data class ApiResponse(
    @SerializedName("id")
    val id: String
)