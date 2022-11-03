package cz.lumonos.intresteremote.service

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import com.beust.klaxon.Parser
import cz.lumonos.intresteremote.data.intreste.CommandCallbackListener
import cz.lumonos.intresteremote.data.intreste.IntresteRepository
import cz.lumonos.intresteremote.service.log.L
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.lang.StringBuilder
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class IntresteCommunicationService {

    companion object {
        const val BLUETOOTH_UUID = "00001101-0000-1000-8000-00805F9B34FB"
        const val BLUETOOTH_NAME = "Intreste"
        const val MAC_ADDRESS = "B8:27:EB:09:2A:7E"

    }

    private val lock = Any()

    private val executor: ExecutorService = Executors.newFixedThreadPool(2)

    private var socket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null
    private var inputStream: InputStream? = null
    private var started = false
    private val messageProtocol = MessageProtocol()
    private var messageCount: Long = 0L

    private var callbackListener: CommandCallbackListener? = null

    fun start() {
        GlobalScope.launch(Dispatchers.IO) {
            while (true) {
                delay(1000)

            }
        }
    }

    fun connect(
        deviceHardwareAddress: String,
        bluetoothAdapter: BluetoothAdapter,
        callbackListener: CommandCallbackListener
    ): Boolean {
        this.callbackListener = callbackListener

        messageProtocol.readMessageCallback = { message ->
            resolveMessage(message)
        }
        callbackListener?.onConnecting()
        GlobalScope.launch(Dispatchers.IO) {


            bluetoothAdapter.cancelDiscovery()
            if (!bluetoothAdapter.isEnabled) bluetoothAdapter.enable()

            try {
                val blueDevice: BluetoothDevice =
                    bluetoothAdapter.getRemoteDevice(deviceHardwareAddress)
                socket =
                    blueDevice.createRfcommSocketToServiceRecord(UUID.fromString(BLUETOOTH_UUID))
                Log.d("GOGOL", "created")
                socket!!.connect()
                Log.d("GOGOL", "connected")
                outputStream = socket!!.outputStream
                inputStream = socket!!.inputStream
                GlobalScope.launch(Dispatchers.Main) { callbackListener.onConnected() }
                GlobalScope.launch(Dispatchers.Main) {
                    while (socket != null){
                        delay(1000)
                        IntresteRepository.instance.requestState()
                    }
                }
                GlobalScope.launch(Dispatchers.Main) {
                    while (socket != null){
                        delay(500)
                        IntresteRepository.instance.requestCurrentGame()
                    }
                }
                GlobalScope.launch(Dispatchers.IO) {
                    startReadTask()
                }


            } catch (e: IOException) {
                Log.d("GOGOL", e.localizedMessage ?: "")
                e.printStackTrace()
                closeConnection()

            }
        }
        return true
    }

    private fun startReadTask() {
        while (true) {
            try {
                val available = inputStream?.available() ?: 0
                if (available <= 0) {
                    continue
                }
                val buffer = ByteArray(available)
                val bytes = inputStream?.read(buffer, 0, available) ?: 0

                messageProtocol.readBytes(buffer)

            } catch (e: IOException) {
                e.printStackTrace()
                L.info(e.message ?: "unknow message")
                closeConnection()
                break
            }
        }
    }

    private fun resolveMessage(message: MessageProtocol.Message) {
        if (message.type == MessageProtocol.DATA) {
            val parser: Parser = Parser.default()
            val jsonObject: JsonObject? = parser.parse(StringBuilder(message.data)) as JsonObject?
            if (jsonObject == null) {
                sendMessage(MessageProtocol.RESPONSE_ERROR, null)
                return
            }
            callbackListener?.onDataReceived(message.type, jsonObject)
            sendMessage(MessageProtocol.RESPONSE_OK, null)
        }
        if (message.type == MessageProtocol.PING) {
            callbackListener?.onPingReceived()
            sendMessage(MessageProtocol.PONG, null)
        }
        if (message.type == MessageProtocol.RESPONSE_OK) {
            callbackListener?.onResponseOkReceived(message.id)
        }

    }


    private fun closeConnection() {
        outputStream?.close()
        inputStream?.close()
        socket?.close()
        outputStream = null
        inputStream = null
        socket = null
        GlobalScope.launch(Dispatchers.Main) { callbackListener?.onDisconnected() }

    }

    fun write(message: String) {
        executor.submit {
            try {

                val bytes = message.toByteArray()
                outputStream!!.write(bytes)
            } catch (e: IOException) {
                L.info(e.message ?: "xxxxx")
                e.printStackTrace()
            }
        }
    }

    fun disconnect(): Boolean {
        closeConnection()
        return true
    }

    fun isConnected(): Boolean {
        return socket?.isConnected ?: false
    }

    fun sendMessage(type: Int, data: JsonObject?): Boolean {
        val dataString = data?.toJsonString()
        val message = MessageProtocol.Message(
            0,
            type,
            System.currentTimeMillis(),
            dataString?.length ?: 2,
            dataString ?: "{}"
        )
        val packagedMessage = messageProtocol.packMessage(message)
        write(packagedMessage)
        messageCount++;
        return true
    }

}