package cz.lumonos.intresteremote

import android.app.Application
import cz.lumonos.intresteremote.service.IntresteCommunicationService
import cz.lumonos.intresteremote.service.log.LoggingService

class App: Application() {

    companion object{
        val loggingService: LoggingService by lazy { LoggingService() }
        val intresteService: IntresteCommunicationService by lazy { IntresteCommunicationService() }
    }

    override fun onCreate() {
        super.onCreate()

    }
}