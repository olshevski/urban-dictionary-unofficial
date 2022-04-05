package dev.olshevski.udu.data

import org.koin.dsl.module

val dataModule = module {
    single { DefinitionsRepository(get(), get()) }
    single { VotesCache() }

    // every Pager instance has its own cache instance
    factory { DefinitionsCache() }
}