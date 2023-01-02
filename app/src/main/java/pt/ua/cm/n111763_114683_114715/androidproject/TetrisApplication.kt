package pt.ua.cm.n111763_114683_114715.androidproject

import android.app.Application
import timber.log.Timber

class TetrisApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Timber library
        Timber.plant(Timber.DebugTree())
    }
}