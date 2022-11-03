package cz.lumonos.intresteremote.data.intreste

import com.beust.klaxon.JsonObject


interface CommandCallbackListener {

    fun onDataReceived(type: Int, jsonObject: JsonObject)
    fun onPingReceived()
    fun onResponseOkReceived(messageId: Long)
    fun onDisconnected()
    fun onConnected()
    fun onConnecting()
}