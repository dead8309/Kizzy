package kizzy.gateway.entities.op

import com.google.gson.*
import java.lang.reflect.Type

class OpSerializer : JsonSerializer<OpCodes>, JsonDeserializer<OpCodes> {
    override fun serialize(
        src: OpCodes?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src?.value)
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): OpCodes? {
        return OpCodes.values().firstOrNull { it.value == json?.asInt }
    }
}