package dev.olshevski.udu.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.ZonedDateTime

@JsonClass(generateAdapter = true)
data class Definition(
    @Json(name = "definition")
    val definition: String,
    @Json(name = "date")
    val date: String?,
    @Json(name = "permalink")
    val permalink: String,
    @Json(name = "thumbs_up")
    val thumbsUp: Int,
    @Json(name = "sound_urls")
    val soundUrls: List<Any>,
    @Json(name = "author")
    val author: String,
    @Json(name = "word")
    val word: String,
    @Json(name = "defid")
    val defId: Long,
    @Json(name = "current_vote")
    val currentVote: Direction?,
    @Json(name = "written_on")
    val writtenOn: ZonedDateTime,
    @Json(name = "example")
    val example: String,
    @Json(name = "thumbs_down")
    val thumbsDown: Int
)

fun List<Definition>.updateVotes(votes: Map<Long, Direction>): List<Definition> =
    if (votes.isEmpty()) {
        this
    } else {
        this.map { it.updateVote(votes) }
    }

fun Definition.updateVote(votes: Map<Long, Direction>): Definition {
    val currentVote = this.currentVote
    val storedVote = votes[this.defId]
    return if (storedVote == null || currentVote == storedVote) {
        this
    } else {
        val (thumbUpIncrement, thumbDownIncrement) =
            when {
                currentVote == Direction.DOWN && storedVote == Direction.UP -> 1 to -1
                currentVote == Direction.UP && storedVote == Direction.DOWN -> -1 to 1
                currentVote == null && storedVote == Direction.UP -> 1 to 0
                currentVote == null && storedVote == Direction.DOWN -> 0 to 1
                else -> error("unsupported case: currentVote=$currentVote storedVote=$storedVote")
            }
        this.copy(
            currentVote = storedVote,
            thumbsUp = this.thumbsUp + thumbUpIncrement,
            thumbsDown = this.thumbsDown + thumbDownIncrement
        )
    }
}