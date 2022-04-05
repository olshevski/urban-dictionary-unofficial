package dev.olshevski.udu.network.typeadapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import dev.olshevski.udu.network.model.Direction

class DirectionAdapter {

    private companion object {
        const val UP = "up"
        const val DOWN = "down"
    }

    @FromJson
    fun fromJson(direction: String): Direction? = when (direction) {
        UP -> Direction.UP
        DOWN -> Direction.DOWN
        else -> null
    }

    @ToJson
    fun toJson(direction: Direction): String = when (direction) {
        Direction.UP -> UP
        Direction.DOWN -> DOWN
    }

}