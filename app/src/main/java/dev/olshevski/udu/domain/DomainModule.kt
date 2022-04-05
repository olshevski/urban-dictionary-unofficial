package dev.olshevski.udu.domain

import org.koin.dsl.module

val domainModule = module {
    factory { GetWordsOfTheDay(get()) }
    factory { GetRandomWords(get()) }
    factory { GetDefinitions(get(), get()) }
    factory { GetSearchSuggestions(get()) }
    factory { VoteForDefinition(get()) }
}