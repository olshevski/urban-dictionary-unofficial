package dev.olshevski.udu

import android.app.Application
import dev.olshevski.udu.data.dataModule
import dev.olshevski.udu.domain.domainModule
import dev.olshevski.udu.network.networkModule
import dev.olshevski.udu.ui.viewModelModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class UDApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        startKoin {
            androidContext(this@UDApplication)
            modules(viewModelModule, domainModule, dataModule, networkModule)
        }
    }

}

val appScope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())