package it.fast4x.rimusic

import android.app.Application
import androidx.compose.runtime.getValue
import app.kreate.android.Preferences
import it.fast4x.rimusic.utils.CaptureCrash
import it.fast4x.rimusic.utils.FileLoggingTree
import timber.log.Timber
import java.io.File

class MainApplication : Application() {

    override fun onCreate() {
        Preferences.load( this )

        super.onCreate()
        //DatabaseInitializer()
        Dependencies.init(this)

        /**** LOG *********/
        val logEnabled by Preferences.DEBUG_LOG
        if (logEnabled) {
            val dir = filesDir.resolve("logs").also {
                if (it.exists()) return@also
                it.mkdir()
            }

            Thread.setDefaultUncaughtExceptionHandler(CaptureCrash(dir.absolutePath))

            Timber.plant(FileLoggingTree(File(dir, "RiMusic_log.txt")))
            Timber.d("Log enabled at ${dir.absolutePath}")
        } else {
            Timber.uprootAll()
            Timber.plant(Timber.DebugTree())
        }
        /**** LOG *********/
    }

    override fun onTerminate() {
        Preferences.unload()

        super.onTerminate()
    }
}

object Dependencies {
    lateinit var application: MainApplication
        private set

    internal fun init(application: MainApplication) {
        this.application = application
    }
}