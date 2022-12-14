package com.mch.blekot.ble

import android.annotation.SuppressLint
import android.bluetooth.*
import android.util.Log
import com.mch.blekot.io.socket.welock.WeLock
import com.mch.blekot.services.SocketSingleton
import com.mch.blekot.util.Constants
import com.mch.blekot.util.HexUtil
import com.mch.blekot.util.UtilDevice
import java.util.*

object Ble {

    private lateinit var mCode: String

    private lateinit var gattTmp: BluetoothGatt

    private val TAG = "Ble.kt"

    private val SERVICE_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e")
    private val NOTIFY_CHARACTERISTIC = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e")
    private val WRITE_CHARACTER = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e")
    private val CCCD_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

    private var characteristicNotify: BluetoothGattCharacteristic? = null
    private var characteristicWrite: BluetoothGattCharacteristic? = null

    private lateinit var mBluetoothGatt: BluetoothGatt

    private lateinit var mDataQueue: Queue<ByteArray>

    /*--------------------------BLE--------------------------*/

    fun writeChar(gatt: BluetoothGatt) {
        val dataIn = HexUtil.hexStringToBytes(mCode)
        mDataQueue = HexUtil.splitByte(dataIn, Constants.MAX_SEND_DATA)
        Log.i(TAG, "SIZE: ${mDataQueue.size}")
        writeDataDevice(gatt)
    }

    object BleDevice {
        private val adapter = BluetoothAdapter.getDefaultAdapter()
        val gatt: BluetoothDevice? = adapter.let { adapter ->
            try {
                return@let adapter.getRemoteDevice(Constants.MAC_ADDRESS)
            }catch (exception: IllegalArgumentException){
                Log.w(TAG, "Objeto no encontrado, buscando dispositivo...")
                return@let null
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun connectDevice() {

        mCode = "5530"

        if (BleDevice.gatt != null) {
            BleDevice.gatt.connectGatt(null, true, mGattCallback)
        }else{
            val btAdapter = BluetoothAdapter.getDefaultAdapter()
            btAdapter?.let { adapter ->
                try {
                    val device = adapter.getRemoteDevice(Constants.MAC_ADDRESS)
                    mBluetoothGatt = device.connectGatt(null, true, mGattCallback)
                }catch (exception: IllegalArgumentException){
                    Log.w(TAG, "Dispositivo no encontrado")
                }
            } ?: run {
                Log.w(TAG, "BluetoothAdapter no inicializado")
            }
        }

    }

    @OptIn(ExperimentalUnsignedTypes::class)
    private val mGattCallback: BluetoothGattCallback = object : BluetoothGattCallback() {

        /*-----------------------1??-----------------------*/

        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                gatt.discoverServices()
                Log.i(TAG, "Conectado! Buscando servicios...")
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.w(TAG, "Desconectado")
            }
        }

        /*-----------------------2??-----------------------*/
        /*Nos suscribimos a las notificaciones y escribimos el descriptor*/

        @SuppressLint("MissingPermission")
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)
            characteristicNotify =
                gatt.getService(SERVICE_UUID).getCharacteristic(NOTIFY_CHARACTERISTIC)
            gatt.setCharacteristicNotification(characteristicNotify, true)
            val desc = characteristicNotify!!.getDescriptor(CCCD_UUID)
            desc.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            gatt.writeDescriptor(desc)
        }

        /*-----------------------3??-----------------------*/

        override fun onDescriptorWrite(
            gatt: BluetoothGatt,
            descriptor: BluetoothGattDescriptor,
            status: Int
        ) {
            if (descriptor.characteristic === characteristicNotify) {
                gattTmp = gatt
                writeChar(gattTmp)
            } else Log.i(TAG, "onDescriptorWrite: Descriptor is not connected")
        }

        /*-----------------------4??-----------------------*/

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            if (characteristicWrite === characteristic) {
                Log.i("Char Writed", "Char: ${characteristic.value.toHexString()}")
            } else Log.i(TAG, "ERROR: Write is not ok")

            writeDataDevice(gatt)
        }

        /*-----------------------5??-----------------------*/

        /* Al recibir la respuesta de la manija lanzamos la request http*/


        @SuppressLint("MissingPermission")
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            if (characteristic.value[0].toInt() == 85 && characteristic.value[1].toInt() == 48) {
                val rndNumber = characteristic.value[2].toUByte().toInt()
                val devicePower = characteristic.value[3].toUByte().toInt()
                val myJason = "{\"rndNumber\":$rndNumber, \"battery\":$devicePower}"
                Log.i(TAG, "onCharacteristicChanged: $myJason")

                WeLock.getToken(devicePower.toString(), rndNumber.toString())

                return

            } else if (characteristic.value[0].toInt() == 85 &&
                characteristic.value[1].toInt() == 49
            ) {
                if (characteristic.value[2].toInt() == 1) {
                    //sendResponse("Success")
                    Log.i(TAG, "")
                } else Log.i(TAG, "ERROR")
            }
            Log.i(TAG, "onCharacteristicChanged: Received")

            // Finaliza la accion con el bluetooth
            SocketSingleton.getSocketInstance().isProcessActive = false
            UtilDevice.sendResponseToServer(
                Constants.CODE_MSG_OK,
                characteristic.value[2].toInt(),
                characteristic.value[3].toInt()
            )
            gattTmp.close()
        }

    }

    @SuppressLint("MissingPermission")
    fun disconnectGatt() {
        gattTmp.close()
    }

    @SuppressLint("MissingPermission")
    fun writeDataWeLockResponse(code: String) {
        try {
            if (code.trim().isEmpty()) {
                throw Exception("Error code es vacio!!")
            }
            mCode = code
            writeChar(gattTmp)
        } catch (e: Exception) {
            // Si hay algun error al obtener respuesta desde WeLock
            // Desconectamos el gatt y permitimos otra peticion
            Log.e(TAG, e.message.toString())

            SocketSingleton.getSocketInstance().isProcessActive = false
            UtilDevice.sendResponseToServer(status = Constants.CODE_MSG_KO)

            gattTmp.close()
        }
    }


    @SuppressLint("MissingPermission")
    private fun writeDataDevice(gatt: BluetoothGatt) {
        var counter = 1
        while (mDataQueue.peek() != null) {
            if (counter > 1) break
            val data = mDataQueue.poll()

            characteristicWrite = gatt.getService(SERVICE_UUID).getCharacteristic(WRITE_CHARACTER)
            characteristicWrite!!.value = data
            //Log.i(TAG, "Sending: ${characteristicWrite!!.value.toHexString()}")
            Log.i(TAG, "Sending: ${HexUtil.formatHexString(characteristicWrite!!.value, true)}")
            characteristicWrite!!.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
            gatt.writeCharacteristic(characteristicWrite)
            counter++
        }
    }

    /*****************UTIL****************/
    @ExperimentalUnsignedTypes
    fun ByteArray.toHexString() =
        asUByteArray().joinToString("") { it.toString(16).padStart(2, '0') }

}