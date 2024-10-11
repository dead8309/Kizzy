package kizzy.gateway.entities.presence

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Party(
    @SerialName("id")
    val id: String = "kizzy",
    @SerialName("size")
    val size: Array<Int> = arrayOf(0, 0)
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Party

        if (id != other.id) return false
        if (!size.contentEquals(other.size)) return false

        return true
    }
    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + size.contentHashCode()
        return result
    }
}