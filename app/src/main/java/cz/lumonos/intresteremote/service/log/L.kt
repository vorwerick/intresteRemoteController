package cz.lumonos.intresteremote.service.log

import cz.lumonos.intresteremote.App

class L {

    companion object{

        fun info(message: String){
            App.loggingService.logInfo(message)
        }

        fun error(message: String){
            App.loggingService.logError(message)
        }
    }
}