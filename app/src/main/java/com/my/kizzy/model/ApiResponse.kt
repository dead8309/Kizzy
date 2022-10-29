package com.my.kizzy.model
import com.google.gson.annotations.SerializedName


data class ApiResponse(
    @SerializedName("id")
    val id: String
)