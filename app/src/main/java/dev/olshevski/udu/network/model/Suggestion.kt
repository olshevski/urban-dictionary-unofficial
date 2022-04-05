package dev.olshevski.udu.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Suggestion(
    @Json(name = "preview")
    val preview: String,
    @Json(name = "term")
    val term: String
)