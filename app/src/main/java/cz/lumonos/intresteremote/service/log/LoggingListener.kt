package cz.lumonos.intresteremote.service.log

interface LoggingListener {

    fun onLogMessage(message: String)
}