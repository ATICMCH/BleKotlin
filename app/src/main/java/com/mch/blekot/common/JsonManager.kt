package com.mch.blekot.common

import android.util.Log
import android.annotation.SuppressLint
import com.mch.blekot.model.DeviceData
import com.mch.blekot.model.Interactor
import com.mch.blekot.model.socket.SocketSingleton

import java.util.*

object JsonManager {

    private const val PATH_CARD = "/API/Device/DeviceCardCommand"
    private const val PATH_OPEN = "/API/Device/DeviceUnLockCommand"
    private const val PATH_SYNC_TIME = "/API/Device/DeviceSyncTime"
    private const val PATH_READ_RECORD = "/API/Device/UnlockRecord"
    private const val PATH_CODE = "/API/Device/DeviceSetTemporaryPassword"

    fun getPostData(action: Int, devicePower: String, rdmNumber: String): Map<String, String> {

        Log.i("JsonManager", "action: $action")

        when (action) {

            Constants.OPEN_LOCK -> {

                val json = """{
                    appID: "WELOCK2202161033", 
                    deviceNumber: "${DeviceData.DEVICE_ID_NUMBER}", 
                    deviceBleName: "${DeviceData.DEVICE_NAME}", 
                    deviceRandomFactor: "$rdmNumber"}
                """.trimIndent()

                return mapOf("json" to json, "path" to PATH_OPEN)
            }

            Constants.NEW_CODE -> {

                //28800 = 8h -> Diferencia con China (bug de la API de Welock)
                //7200 = 2h -> Para asegurarse que el codigo este funcional
                val startDate: Int = ((System.currentTimeMillis() / 1000) - 28800 - 7200).toInt()
                val endDate: Int = startDate + (86400 * Interactor.getDays())

                SocketSingleton.socketInstance?.startTime = startDate.toString()
                SocketSingleton.socketInstance?.endTime = endDate.toString()

                /**
                 * Los valores de index y times los recibimos por socket.
                 * En distintos index podemos almacenar distintos códigos.
                 * */

                val json = """{
                    appID: "WELOCK2202161033", 
                    deviceNumber: "${DeviceData.DEVICE_ID_NUMBER}",  
                    deviceBleName: "${DeviceData.DEVICE_NAME}", 
                    deviceRandomFactor: "$rdmNumber", 
                    password: ${Interactor.getDeviceNewPassword()}, 
                    index: ${Interactor.getIndex()}, 
                    user: ${Constants.CODE_USER} , 
                    times: ${Interactor.getTimes()},
                    startTimestamp: $startDate, 
                    endTimestamp: $endDate}
                """.trimIndent()

                return mapOf("json" to json, "path" to PATH_CODE)
            }

            Constants.SET_CARD -> {

                val json = """{
                    appID: "WELOCK2202161033",
                    deviceNumber: "${DeviceData.DEVICE_ID_NUMBER}",
                    deviceBleName: "${DeviceData.DEVICE_NAME}",
                    deviceRandomFactor: "$rdmNumber",
                    cardQr: "${Interactor.getQR()}",
                    type: "${Interactor.getType()}"}""".trimIndent()

                return mapOf("json" to json, "path" to PATH_CARD)
            }

            Constants.SYNC_TIME -> {
                val json = """{
                    appID: "WELOCK2202161033",
                    deviceNumber: "${DeviceData.DEVICE_ID_NUMBER}",
                    deviceBleName: "${DeviceData.DEVICE_NAME}",
                    timestamp: ${Interactor.getNewTime()},
                    deviceRandomFactor: "$rdmNumber"}""".trimIndent()

                Interactor.setAction(Constants.SYNC_TIME_OK)

                return mapOf("json" to json, "path" to PATH_SYNC_TIME)
            }

            Constants.READ_RECORD -> {
                val json = """{
                    appID: "WELOCK2202161033", 
                    deviceNumber: "${DeviceData.DEVICE_ID_NUMBER}", 
                    deviceBleName: "${DeviceData.DEVICE_NAME}", 
                    devicePower: "$devicePower", 
                    deviceRandomFactor: "$rdmNumber"}
                """.trimIndent()

                return mapOf("json" to json, "path" to PATH_READ_RECORD)
            }

            else -> {
                Log.e("JsonManager", "Ninguna accion declarada")
                return mapOf("Error" to "ERROR")
            }
        }
    }

    fun getServerResponseJson(
            status: Int, statusMOne: Int = Constants.STATUS_LOCK,
            statusMTwo: Int = Constants.STATUS_LOCK,
            phoneBattery: Int? = null, deviceBattery: Int? = null,
            isCharging: Boolean? = null, action: Int
    ): String {

        val time = getTime()
        Log.i("date", time)

        val msg: String = when (status) {
            Constants.CODE_MSG_OK -> Constants.MSG_OK
            Constants.CODE_MSG_PENDANT -> Constants.MSG_PENDANT
            Constants.CODE_MSG_PARAMS -> Constants.MSG_PARAMS
            Constants.CODE_MSG_NULL_POINT -> Constants.MSG_NULL_POINT
            Constants.CODE_MSG_BLE_OFF -> Constants.MSG_BLE_OFF
            Constants.CODE_TIMEOUT -> Constants.MSG_TIMEOUT
            Constants.CODE_TIMEOUT_SCAN -> Constants.MSG_TIMEOUT_SCAN
            else -> Constants.MSG_KO
        }

        val responseJson: String

        when (action) {

            Constants.NEW_CODE -> {
                responseJson = """{ 
                    "status":$status,
                    "statusMOne":$statusMOne,
                    "statusMTwo":$statusMTwo,
                    "msg":"$msg",
                    "clientFrom":"${SocketSingleton.socketInstance?.clientFromServer}",
                    "startTime":"${SocketSingleton.socketInstance?.startTime}",
                    "endTime":"${SocketSingleton.socketInstance?.endTime}",
                    "lockBattery": $deviceBattery,
                    "date" : "${getTime()}"
                    }""".trimIndent()
            }

            Constants.GET_BATTERY -> {
                responseJson = """{
                     "status":$status,
                     "clientFrom":"${SocketSingleton.socketInstance?.clientFromServer}",
                     "phoneBattery": $phoneBattery,
                     "isCharging": $isCharging,
                     "lockBattery": $deviceBattery,
                     "date" : "${getTime()}"
                     }""".trimIndent()
            }

            else -> {
                responseJson = """{
                     "status":$status,
                     "statusMOne":$statusMOne,
                     "statusMTwo":$statusMTwo,
                     "msg":"$msg",
                     "clientFrom":"${SocketSingleton.socketInstance?.clientFromServer}",
                     "lockBattery": $deviceBattery,
                     "date" : "${getTime()}"
                     }""".trimIndent()
            }
        }
        return responseJson
    }


    @SuppressLint("SimpleDateFormat")
    fun getCredentialsResponse(
            status: Int,
            deviceId: String?,
            deviceName: String?,
            macAddress: String?,
            urlArduino: String?
    ): String {
        if (urlArduino == null) {
            return """
            { "clientFrom" : "${SocketSingleton.socketInstance?.clientFromServer}",
             "status" : $status,
             "deviceId" : "$deviceId",
             "deviceName" : "$deviceName",
             "macAddress" : "$macAddress",
              "msg" : "Error en las credenciales",
              "date" : "${getTime()}"}
    """.trimIndent()
        } else {
            return """
            { "clientFrom" : "${SocketSingleton.socketInstance?.clientFromServer}",
             "status" : $status,
             "urlArduino" : "$urlArduino"
              "msg" : "Error en las credenciales",
              "date" : "${getTime()}"}
    """.trimIndent()
        }
    }

}