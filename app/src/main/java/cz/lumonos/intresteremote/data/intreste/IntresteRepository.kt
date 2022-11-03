package cz.lumonos.intresteremote.data.intreste

import android.bluetooth.BluetoothAdapter
import android.util.Log
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
    private var stateCallbacks = mutableListOf<(currentGame: State?) -> Unit>()
    private var connectionCallbacks = mutableListOf<(connectionStatus: Int, address: String) -> Unit>()

    var address = ""

    fun connect(macAddress: String, bluetoothAdapter: BluetoothAdapter): Boolean {
        address = macAddress
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

    fun subscribeState(callback: (State?) -> Unit) {
        stateCallbacks.add(callback)
    }

    fun subscribeConnectionStatus(callback: (Int?, String) -> Unit) {
        connectionCallbacks.add(callback)
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
                val newObject = jsonObject["currentGame"]
                if (newObject != null) {
                    val currentGame = Klaxon().parse<CurrentGame?>(newObject as String)
                    Log.i("Data received", "current game: " + jsonObject.toJsonString())

                    currentGameCallbacks.forEach { it.invoke(currentGame) }
                }

            }
            if (jsonObject["command"] == "state") {
                val newObject = jsonObject["state"]

                if (newObject != null) {
                    val state = Klaxon().parse<State?>(jsonObject["state"] as String)
                    Log.i("Data received", "state: " + jsonObject.toJsonString())

                    stateCallbacks.forEach { it.invoke(state) }
                }

            }
        }
    }

    override fun onPingReceived() {
    }

    override fun onResponseOkReceived(messageId: Long) {
    }

    override fun onDisconnected() {
        connectionCallbacks.forEach { it.invoke(0, address) }
    }

    override fun onConnected() {
        connectionCallbacks.forEach { it.invoke(1, address) }
    }

    override fun onConnecting() {
        connectionCallbacks.forEach { it.invoke(2, address) }
    }

    fun runLedDisplayTest() {
        App.intresteService.sendMessage(
            MessageProtocol.DATA,
            JsonObject(mapOf("command" to "startLedTest"))
        )
    }

    fun runPanelTest() {
        App.intresteService.sendMessage(
            MessageProtocol.DATA,
            JsonObject(mapOf("command" to "startPanelTest"))
        )
    }

    fun requestState() {
        App.intresteService.sendMessage(
            MessageProtocol.DATA,
            JsonObject(mapOf("command" to "getState"))
        )
    }

    fun restartGame() {
        App.intresteService.sendMessage(
            MessageProtocol.DATA,
            JsonObject(mapOf("command" to "restartGame"))
        )
    }

}