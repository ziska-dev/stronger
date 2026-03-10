package org.ziskadev.stronger

import android.app.Application
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.ziskadev.stronger.di.appModules

class StrongerApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Logging initialisieren
        Napier.base(DebugAntilog())

        // Koin starten
        startKoin {
            androidLogger()
            androidContext(this@StrongerApplication)
            modules(appModules)
        }
    }
}