package cz.lumonos.intresteremote.data.intreste

import android.bluetooth.BluetoothAdapter
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import cz.lumonos.intresteremote.App
import cz.lumonos.intresteremote.service.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class IntresteRepository() : CommandCallbackListener {

    companion object {
        val instance = IntresteRepository()
    }

    private var currentGameCallbacks = mutableListOf<(currentGame: CurrentGame?) -> Unit>()

    fun connect(macAddress: String, bluetoothAdapter: BluetoothAdapter): Boolean {
        return App.intresteService.connect(
            macAddress,
            bluetoothAdapter,
            this
        )
    }

    fun disconnect(): Boolean {
        return App.intresteService.disconnect()
    }

    fun isConnected(): Boolean {
        return App.intresteService.isConnected()
    }


    fun startGame(config: ObjectGameConfig) {
        App.intresteService.sendMessage(
            MessageProtocol.DATA,
            JsonObject(
                mapOf(
                    "command" to "startGame",
                    "hitPoints" to config.hitPoints,
                    "missesPoints" to config.missesPoints,
                    "timeout" to config.timeout,
                )
            )
        )
    }

    fun cancelGame() {
        App.intresteService.sendMessage(
            MessageProtocol.DATA,
            JsonObject(mapOf("command" to "interruptGame"))
        )
    }

    fun sortPanels() {
        App.intresteService.sendMessage(
            MessageProtocol.DATA,
            JsonObject(mapOf("command" to "sortPanels"))
        )
    }

    fun cancelSortPanels() {
        App.intresteService.sendMessage(
            MessageProtocol.DATA,
            JsonObject(mapOf("command" to "interruptSortPanels"))
        )
    }

    fun subscribeCurrentGame(callback: (CurrentGame?) -> Unit) {
        currentGameCallbacks.add(callback)
    }

    fun requestCurrentGame() {
        App.intresteService.sendMessage(
            MessageProtocol.DATA,
            JsonObject(mapOf("command" to "getCurrentGame"))
        )
    }

    override fun onDataReceived(type: Int, jsonObject: JsonObject) {
        GlobalScope.launch(Dispatchers.Main) {
            if (jsonObject["command"] == "currentGame") {
               val currentGame = Klaxon().parse<CurrentGame?>(jsonObject["currentGame"] as String)
                currentGameCallbacks.forEach { it.invoke(currentGame) }
            }
        }
    }

    override fun onPingReceived() {
    }

    override fun onResponseOkReceived(messageId: Long) {
    }

}