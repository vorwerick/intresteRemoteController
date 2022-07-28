package cz.lumonos.intresteremote.service.log

class LoggingService {

    var loggingListener: LoggingListener? = null


    fun logError(message: String) {
        loggingListener?.onLogMessage(message)
    }

    fun logInfo(message: String) {
        loggingListener?.onLogMessage(message)

    }




}