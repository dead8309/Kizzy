/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * UpdateEventSerializer.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.domain.model.samsung_rpc

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class UpdateEventSerializer: KSerializer<UpdateEvent> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("UpdateEvent", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): UpdateEvent {
        val updateEvent = decoder.decodeString()
        return UpdateEvent.values().firstOrNull { it.value == updateEvent } ?: throw IllegalArgumentException("Unknown event $updateEvent")
    }

    override fun serialize(encoder: Encoder, value: UpdateEvent) {
        encoder.encodeString(value.value)
    }
}