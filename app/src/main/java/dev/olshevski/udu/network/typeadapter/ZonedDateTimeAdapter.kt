package dev.olshevski.udu.network.typeadapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.time.ZonedDateTime

class ZonedDateTimeAdapter {

    @FromJson
    fun fromJson(time: String): ZonedDateTime = ZonedDateTime.parse(time)

    @ToJson
    fun toJson(time: ZonedDateTime): String = TODO("unused")

}