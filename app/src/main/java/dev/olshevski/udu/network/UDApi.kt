package dev.olshevski.udu.network

import dev.olshevski.udu.network.model.Definition
import dev.olshevski.udu.network.model.DefinitionList
import dev.olshevski.udu.network.model.SuggestionResults
import dev.olshevski.udu.network.model.VoteRequest
import kotlinx.coroutines.delay
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.io.IOException
import java.time.ZonedDateTime
import kotlin.random.Random

interface UDApi {

    @GET("v0/words_of_the_day")
    suspend fun getWordsOfTheDay(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): DefinitionList

    @GET("v0/define")
    suspend fun getDefinitions(
        @Query("term") term: String
    ): DefinitionList

    @GET("v0/define")
    suspend fun getDefinitions(
        @Query("defid") defId: Long
    ): DefinitionList

    @GET("v0/random")
    suspend fun getRandomWords(): DefinitionList

    @GET("v0/autocomplete-extra")
    suspend fun getSearchSuggestions(
        @Query("term") term: String
    ): SuggestionResults

    @POST("v0/vote")
    suspend fun voteForDefinition(@Body voteRequest: VoteRequest)
}

class FakeUDApi : UDApi {
    override suspend fun getWordsOfTheDay(page: Int, perPage: Int): DefinitionList {
        throw IOException()
//        delay(3000)
//        return when (page) {
//            //2 -> throw IOException()
//            100 -> generateDefinitions(page, perPage, perPage / 2)
//            else -> generateDefinitions(page, perPage, perPage)
//        }
    }

    override suspend fun getDefinitions(term: String): DefinitionList {
        throw IOException()
//        delay(3000)
//        return generateDefinitions(1, 10, 10)
    }

    override suspend fun getDefinitions(defId: Long): DefinitionList {
        TODO("Not yet implemented")
    }

    override suspend fun getRandomWords(): DefinitionList {
        delay(3000)
        return generateDefinitions(Random.nextInt(1, 1000), 10, 10)
    }

    override suspend fun getSearchSuggestions(term: String): SuggestionResults {
        return SuggestionResults(emptyList())
    }

    override suspend fun voteForDefinition(voteRequest: VoteRequest) {
    }

    private fun generateDefinitions(page: Int, perPage: Int, count: Int) =
        DefinitionList(
            Array(count) {
                val id = (page - 1) * perPage + it
                Definition(
                    definition = "[definition] $id",
                    date = "",
                    permalink = "",
                    thumbsUp = 0,
                    soundUrls = emptyList(),
                    author = "",
                    word = "page $page word $id",
                    defId = id.toLong(),
                    currentVote = null,
                    writtenOn = ZonedDateTime.now(),
                    example = "example $id",
                    thumbsDown = 0
                )
            }.toList()
        )

}