package dev.olshevski.udu.network

import com.squareup.moshi.Moshi
import dev.olshevski.udu.BuildConfig
import dev.olshevski.udu.network.typeadapter.DirectionAdapter
import dev.olshevski.udu.network.typeadapter.ZonedDateTimeAdapter
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

private const val USE_FAKE_API = false

val networkModule = module {
    single {
        @Suppress("ConstantConditionIf")
        if (USE_FAKE_API) {
            FakeUDApi()
        } else {
            val client = OkHttpClient.Builder().apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(
                        HttpLoggingInterceptor().apply {
                            setLevel(HttpLoggingInterceptor.Level.BODY)
                        }
                    )
                }
            }.build()

            val moshi = Moshi.Builder()
                .add(ZonedDateTimeAdapter())
                .add(DirectionAdapter())
                .build()

            Retrofit.Builder()
                .client(client)
                .baseUrl("https://api.urbandictionary.com")
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(UDApi::class.java)
        }
    }
}