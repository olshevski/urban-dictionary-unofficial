package dev.olshevski.udu.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DefinitionList(
    @Json(name = "list")
    val list: List<Definition>
)