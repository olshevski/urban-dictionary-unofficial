package dev.olshevski.udu.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VoteRequest(
    @Json(name = "defid")
    val defId: Long,
    @Json(name = "direction")
    val direction: Direction
)