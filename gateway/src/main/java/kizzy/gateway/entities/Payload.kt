package kizzy.gateway.entities


import com.google.gson.annotations.SerializedName
import kizzy.gateway.entities.op.OpCodes

data class Payload(
    @SerializedName("t")
    val t: Any? = null,
    @SerializedName("s")
    val s: Int? = null,
    @SerializedName("op")
    val op: OpCodes? = null,
    @SerializedName("d")
    val d: Any? = null
)