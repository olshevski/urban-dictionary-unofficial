package dev.olshevski.udu.domain

import androidx.paging.Pager
import dev.olshevski.udu.data.DefinitionsRepository
import dev.olshevski.udu.domain.base.GetPagingDataInteractor
import dev.olshevski.udu.network.model.Definition

class GetWordsOfTheDay(private val definitionsRepository: DefinitionsRepository) :
    GetPagingDataInteractor<Definition>() {

    override fun getPager(): Pager<Int, Definition> = definitionsRepository.getWordsOfTheDay()

}
