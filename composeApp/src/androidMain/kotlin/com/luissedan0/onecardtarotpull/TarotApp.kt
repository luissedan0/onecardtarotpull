package com.luissedan0.onecardtarotpull

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import com.luissedan0.onecardtarotpull.di.appModule
import com.luissedan0.onecardtarotpull.di.androidModule

/**
 * Android [Application] subclass responsible for bootstrapping Koin DI.
 *
 * Koin is started here (rather than in [MainActivity]) so that it is initialised
 * exactly once — even if the Activity is recreated — and before any Activity or
 * Service tries to inject dependencies.
 *
 * Registered in AndroidManifest.xml via `android:name=".TarotApp"`.
 */
class TarotApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Must be set before Koin resolves any singleton that calls AppContextHolder.context
        // (i.e. getDatabaseBuilder / createDataStorePath in DatabaseFactory / DataStoreFactory).
        AppContextHolder.init(this)

        startKoin {
            // KMP Koin: use androidLogger only in DEBUG builds to avoid log spam in prod.
            androidLogger(Level.DEBUG)
            androidContext(this@TarotApp)
            modules(appModule, androidModule)
        }
    }
}
