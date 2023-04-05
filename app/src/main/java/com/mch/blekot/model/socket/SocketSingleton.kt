package com.mch.blekot.model.socket

import java.util.*
import java.net.URI
import kotlin.Exception
import android.util.Log
import org.json.JSONArray
import io.socket.client.IO
import org.json.JSONObject
import kotlinx.coroutines.*
import io.socket.client.Socket
import com.mch.blekot.common.Constants
import android.annotation.SuppressLint
import com.mch.blekot.common.ValidateUtil
import com.mch.blekot.common.ActionManager
import com.mch.blekot.services.SocketService
import com.mch.blekot.common.ProcessDataJson
import com.mch.blekot.common.ValidateException

class SocketSingleton private constructor() {

    var isProcessActive = false
    var clientFromServer = ""
    val socket: Socket
    var endTime: String? = null
    var startTime: String? = null

    //Constructor
    init {
        val options = IO.Options()
        options.reconnection = true
        socket = IO.socket(URI.create(Constants.URL_TCP), options)
        socket.on(Socket.EVENT_CONNECT) {
            println("Conectado!!")
            socket.emit(Constants.ACTION_LOG, Constants.ID, Constants.MESSAGE)
        }

        socket.on(Socket.EVENT_CONNECT_ERROR) { args: Array<Any> -> println("connect_error: " + args[0]) }

        /** Aqui recibimos las ordenes y las controlamos **/

        socket.on(Constants.ACTION_ADMIN) { args: Array<Any?>? ->
            val dataResponse: JSONArray
            try {
                if (isProcessActive) {
                    Log.i(TAG, "Hay una peticion pendiente!!")
                    ActionManager.sendResponseToServer(
                        Constants.CODE_MSG_PENDANT,
                        Constants.STATUS_LOCK,
                        Constants.STATUS_LOCK
                    )
                    return@on
                }
                dataResponse = JSONArray(args)
                val dataJson = JSONObject(dataResponse[1].toString())

                // Obtener dataJSON en un HashMap
                val pDataJson = ProcessDataJson()
                pDataJson.getData(dataJson)

                val action = Objects.requireNonNull(pDataJson.getValue("cmd"))
                    .toString()
                clientFromServer = Objects.requireNonNull(pDataJson.getValue("clientFrom"))
                    .toString()
                isProcessActive = true

                /**
                 * Las acciones que incluyan funciones bluetooth validarán en primer lugar
                 * que los parametros (credenciales de la manija) estén correctos y en caso de que
                 * alguno este mal lanzará un error y devolverá un mensaje al socket con el error

                 * Mismo procedimiento para la funcion del arduino que valida la url
                 * */

                when (action) {

                    Constants.ACTION_OPEN_LOCK -> executeAction {

                        val macAddress =
                            Objects.requireNonNull(pDataJson.getValue("macAddress")).toString()

                        val deviceName =
                            Objects.requireNonNull(pDataJson.getValue("deviceName")).toString()

                        val deviceId =
                            Objects.requireNonNull(pDataJson.getValue("deviceId")).toString()

                        ValidateUtil.setUpBle(macAddress, deviceName, deviceId)

                        ActionManager.openLock()
                    }

                    Constants.ACTION_NEW_CODE -> {

                        val macAddress =
                            Objects.requireNonNull(pDataJson.getValue("macAddress")).toString()

                        val deviceName =
                            Objects.requireNonNull(pDataJson.getValue("deviceName")).toString()

                        val deviceId =
                            Objects.requireNonNull(pDataJson.getValue("deviceId")).toString()

                        ValidateUtil.setUpBle(macAddress, deviceName, deviceId)

                        val code = Objects.requireNonNull(pDataJson.getValue("code"))
                            .toString()
                        var days = Objects.requireNonNull(pDataJson.getValue("days"))
                            .toString().toInt()
                        days = if (days == 0) Constants.MIN_DAYS_PASSWORD else days

                        val index =
                            Objects.requireNonNull(pDataJson.getValue("index")).toString().toInt()
                        val times =
                            Objects.requireNonNull(pDataJson.getValue("times")).toString().toInt()

                        executeAction { ActionManager.setNewCode(code, days, index, times) }
                    }

                    Constants.ACTION_SET_CARD -> {

                        val macAddress =
                            Objects.requireNonNull(pDataJson.getValue("macAddress")).toString()

                        val deviceName =
                            Objects.requireNonNull(pDataJson.getValue("deviceName")).toString()

                        val deviceId =
                            Objects.requireNonNull(pDataJson.getValue("deviceId")).toString()

                        ValidateUtil.setUpBle(macAddress, deviceName, deviceId)

                        val qr = Objects.requireNonNull(pDataJson.getValue("Qr"))
                            .toString()
                        val type = Objects.requireNonNull(pDataJson.getValue("type"))
                            .toString()
                        executeAction { ActionManager.setNewCard(qr, type) }
                    }

                    Constants.ACTION_OPEN_PORTAL -> executeAction {
                        val ipArduino =
                            Objects.requireNonNull(pDataJson.getValue("ipArduino")).toString()

                        ValidateUtil.setUpArduino(ipArduino)

                        ActionManager.openPortal()
                    }

                    Constants.ACTION_SYNC_TIME -> {

                        val macAddress =
                            Objects.requireNonNull(pDataJson.getValue("macAddress")).toString()

                        val deviceName =
                            Objects.requireNonNull(pDataJson.getValue("deviceName")).toString()

                        val deviceId =
                            Objects.requireNonNull(pDataJson.getValue("deviceId")).toString()

                        ValidateUtil.setUpBle(macAddress, deviceName, deviceId)

                        val newTime = Objects.requireNonNull(pDataJson.getValue("syncTime"))
                            .toString()
                        executeAction { ActionManager.syncTime(newTime) }
                    }

                    Constants.ACTION_GET_BATTERY -> {

                        val macAddress =
                            Objects.requireNonNull(pDataJson.getValue("macAddress")).toString()

                        val deviceName =
                            Objects.requireNonNull(pDataJson.getValue("deviceName")).toString()

                        val deviceId =
                            Objects.requireNonNull(pDataJson.getValue("deviceId")).toString()

                        ValidateUtil.setUpBle(macAddress, deviceName, deviceId)

                        executeAction { ActionManager.getDevicesBatteries() }
                    }

                }
            } catch (e: ValidateException) {
                socket.emit(
                    Constants.RESPONSE_SOCKET_BLUETOOTH,
                    Constants.ID,
                    ValidateUtil.getResponse()
                )
            } catch (e: java.lang.NullPointerException) {
                Log.i(TAG, "error: ${e.printStackTrace()}")
                ActionManager.sendResponseToServer(
                    Constants.CODE_MSG_NULL_POINT,
                    Constants.STATUS_LOCK,
                    Constants.STATUS_LOCK
                )
            } catch (e: Exception) {
                e.printStackTrace()
                ActionManager.sendResponseToServer(
                    Constants.CODE_MSG_KO,
                    Constants.STATUS_LOCK,
                    Constants.STATUS_LOCK
                )
            }
        }
        socket.on(Socket.EVENT_DISCONNECT) { args: Array<Any> -> println("disconnect due to: " + args[0]) }
        socket.connect()
    }

    fun emitResponse(msg: String){
        socket.emit(
            Constants.RESPONSE_SOCKET_BLUETOOTH,
            Constants.ID,
            msg
        )
        isProcessActive = false
    }

    //coroutines
    private fun executeAction(block: suspend () -> Unit): Job {
        return MainScope().launch(Dispatchers.IO) {
            try {
                block()
            }catch (_: Exception){}
        }
    }

    companion object {
        private val TAG = SocketService::class.java.simpleName

        @SuppressLint("StaticFieldLeak")
        private var mInstance: SocketSingleton? = null

        @get:Synchronized
        val socketInstance: SocketSingleton?
            get() {
                if (mInstance == null) {
                    mInstance = SocketSingleton()
                }
                return mInstance
            }
    }
}