package com.mch.blekot

import android.Manifest
import android.bluetooth.*
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.mch.blekot.util.Constants
import com.mch.blekot.util.HexUtil
import java.util.*


class Ble(private val mContext: Context) {

    private lateinit var mCode: String
    private lateinit var mAction: String
    private lateinit var mNewCode: String

    private lateinit var mDataQueue: Queue<ByteArray>

    private val TAG = "Main Activity"

    private val SERVICE_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e")
    private val NOTIFY_CHARACTERISTIC = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e")
    private val WRITE_CHARACTER = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e")
    private val CCCD_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

    var characteristicNotify: BluetoothGattCharacteristic? = null
    var characteristicWrite: BluetoothGattCharacteristic? = null
    var myGatt: BluetoothGatt? = null

    /*--------------------------BLE--------------------------*/

    /*companion object {
        private @JvmStatic var state: Boolean = false;
    }*/

    fun startBle(code: String, action: String? = "", newCode: String? = null) {

        //Tomamos el codigo y lo guardamos en una variable global
        mCode = code

        if (newCode != null) {
            mNewCode = newCode
        }
        mAction = action!!

        // D6F53BE46DF5 -> 21471175
        val btAdapter = BluetoothAdapter.getDefaultAdapter()
        val device = btAdapter.getRemoteDevice("D6:F5:3B:E4:6D:F5") // C7:12:48:82:08:2F
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) !=
            PackageManager.PERMISSION_GRANTED
        ) {
        }
        device.connectGatt(mContext, true, mGattCallback)
        //device.connectGatt(mContext, true, mGattCallback, TRANSPORT_LE)
        //device.connectGatt(mContext, false, mGattCallback, TRANSPORT_LE)
    }

    private val mGattCallback: BluetoothGattCallback = object : BluetoothGattCallback() {


        /*-----------------------1º-----------------------*/
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                if (ActivityCompat.checkSelfPermission(
                        mContext,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                }
                gatt.discoverServices()
                myGatt = gatt
                Log.i(TAG, "onConnectionStateChange: Discover Services")
            }
        }

        override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {
            super.onMtuChanged(gatt, mtu, status)
            Log.i(TAG, "ATT MTU changed to $mtu, success: ${status == BluetoothGatt.GATT_SUCCESS}");
        }

        /*-----------------------2º-----------------------*/
        /*Nos suscribimos a las notificaciones y escribimos el descriptor*/

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)
            characteristicNotify =
                gatt.getService(SERVICE_UUID).getCharacteristic(NOTIFY_CHARACTERISTIC)
            if (ActivityCompat.checkSelfPermission(
                    mContext,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) == PackageManager.PERMISSION_GRANTED
            ) {
            }
            gatt.setCharacteristicNotification(characteristicNotify, true)
            val desc = characteristicNotify!!.getDescriptor(CCCD_UUID)
            desc.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            gatt.writeDescriptor(desc)
        }
        /*-----------------------3º-----------------------*/
        override fun onDescriptorWrite(
            gatt: BluetoothGatt,
            descriptor: BluetoothGattDescriptor,
            status: Int
        ) {
            if (descriptor.characteristic === characteristicNotify) {
                sendBLE(gatt)
            } else Log.i(TAG, "onDescriptorWrite: Descriptor is not connected")
        }

        fun String.decodeHex(): ByteArray {
            check(length % 2 == 0)
            return chunked(2)
                .map { it.toInt(16).toByte() }
                .toByteArray()
        }

        /*-----------------------4ª-----------------------*/

        // Aqui escribimos las caracteristicas

        fun sendBLE_Tmp(gatt: BluetoothGatt) {
            characteristicWrite = gatt.getService(SERVICE_UUID).getCharacteristic(WRITE_CHARACTER)
            Log.i(TAG, "Prueba00111111")
            val codeHex = mCode.decodeHex()
            val maxByteArraySize = codeHex.size
            val plusOne = if ((maxByteArraySize % 20) > 0) 1 else 0
            if (ActivityCompat.checkSelfPermission(
                    mContext,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
            }
            for (j in 1..maxByteArraySize / 20 + plusOne) {
                if (maxByteArraySize - (j * 20) >= 20) {
                    characteristicWrite!!.value = codeHex.copyOfRange(20 * (j - 1), 20 * j)
                    Log.i(TAG, "Sending: ${characteristicWrite!!.value.toHexString()}")
                }else{
                    characteristicWrite!!.value = codeHex.copyOfRange(20 * (j - 1), maxByteArraySize)
                    Log.i(TAG, "Sending: ${characteristicWrite!!.value.toHexString()}")
                }
                characteristicWrite!!.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT

                gatt.writeCharacteristic(characteristicWrite)
            }
        }

        fun sendBLE(gatt: BluetoothGatt) {
            val dataIn = HexUtil.hexStringToBytes(mCode)
            mDataQueue = HexUtil.splitByte(dataIn, Constants.MAX_SEND_DATA)
            Log.i(TAG, "SIZE: ${mDataQueue.size}")
            writeDataDevice(gatt)
        }

        @ExperimentalUnsignedTypes
        fun ByteArray.toHexString() = asUByteArray().joinToString("") { it.toString(16).padStart(2, '0') }


        private fun writeDataDevice(gatt: BluetoothGatt){
            var counter = 1;
            while(mDataQueue.peek() != null){
                if (counter > 1) break;
                val data = mDataQueue.poll()
                if (ActivityCompat.checkSelfPermission(
                        mContext,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {}
                characteristicWrite = gatt.getService(SERVICE_UUID).getCharacteristic(WRITE_CHARACTER)
                characteristicWrite!!.value = data
                //Log.i(TAG, "Sending: ${characteristicWrite!!.value.toHexString()}")
                Log.i(TAG, "Sending: ${HexUtil.formatHexString(characteristicWrite!!.value, true)}")
                characteristicWrite!!.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                gatt.writeCharacteristic(characteristicWrite)
                counter++;
            }
        }
        /*-----------------------5º-----------------------*/

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            if (characteristicWrite === characteristic) {
                Log.i("Char Write", "Char: $characteristic status: $status")
                if (characteristic === characteristicWrite) {
                    characteristic.value
                    Log.i("Char Write1", "Char: ${characteristic.value.toHexString()}")
                }

                // SEND SOME PACKAGES
                writeDataDevice(gatt)
                // SEND SOME PACKAGES

                // Captura de escritura
                with(characteristic) {
                    when (status) {
                        BluetoothGatt.GATT_SUCCESS -> {
                            Log.i("BluetoothGattCallback", "Wrote to characteristic $uuid | value: ${value.toHexString()}")
                            if (ActivityCompat.checkSelfPermission(
                                    mContext,
                                    Manifest.permission.BLUETOOTH_CONNECT
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {}
                            val readData = gatt.readCharacteristic(characteristic)
                        }
                        BluetoothGatt.GATT_INVALID_ATTRIBUTE_LENGTH -> {
                            Log.e("BluetoothGattCallback", "Write exceeded connection ATT MTU!")
                        }
                        BluetoothGatt.GATT_WRITE_NOT_PERMITTED -> {
                            Log.e("BluetoothGattCallback", "Write not permitted for $uuid!")
                        }
                        BluetoothGatt.GATT_CONNECTION_CONGESTED -> {
                            Log.e("BluetoothGattCallback-GATT_CONNECTION_CONGESTED", "Write not permitted for $uuid!")
                        }
                        BluetoothGatt.GATT_INSUFFICIENT_ENCRYPTION -> {
                            Log.e("BluetoothGattCallback-GATT_INSUFFICIENT_ENCRYPTION", "Write not permitted for $uuid!")
                        }
                        BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION -> {
                            Log.e("BluetoothGattCallback-GATT_INSUFFICIENT_AUTHENTICATION", "Write not permitted for $uuid!")
                        }
                        BluetoothGatt.GATT_INVALID_OFFSET -> {
                            Log.e("BluetoothGattCallback-GATT_INVALID_OFFSET", "Write not permitted for $uuid!")
                        }
                        BluetoothGatt.GATT_FAILURE -> {
                            Log.e("BluetoothGattCallback-GATT_FAILURE", "Write not permitted for $uuid!")
                        }
                        else -> {
                            Log.e("BluetoothGattCallback", "Characteristic write failed for $uuid, error: $status")
                        }
                    }
                }
            } else Log.i(TAG, "ERROR: Write is not ok")
        }

        /*-----------------------6ª-----------------------*/
        /* Al recibir la respuesta de la manija lanzamos la request http*/

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            Log.i(TAG, "tronald - size: ${characteristic.value.size}");
            Log.i(TAG, "tronald0: ${characteristic.value[0].toString()}");
            Log.i(TAG, "tronald1: ${characteristic.value[1].toString()}");
            if (characteristic.value[0].toInt() == 85 && characteristic.value[1].toInt() == 48) {
                val rndNumber = characteristic.value[2].toUByte().toInt()
                val devicePower = characteristic.value[3].toUByte().toInt()
                val myJason = "{\"rndNumber\":$rndNumber, \"battery\":$devicePower}"
                Log.i(TAG, "onCharacteristicChanged: $myJason")

                when (mAction) {
                    Constants.ACTION_OPEN_LOCK ->
                        WeLock("$rndNumber", "$devicePower", mAction, this@Ble)
                            .getToken()

                    Constants.ACTION_NEW_CODE ->
                        WeLock("$rndNumber", "$devicePower", mAction, this@Ble)
                            .getToken(code = mNewCode)

                    Constants.ACTION_SET_CARD ->
                        WeLock("$rndNumber", "$devicePower", mAction, this@Ble)
                            .getToken()
                    "ERROR" ->
                        Log.i(TAG, "Fin ERROR-ERROR-ERROR");
                }
            } else if (characteristic.value[0].toInt() == 85 &&
                characteristic.value[1].toInt() == 49
            ) {
                if (characteristic.value[2].toInt() == 1)
                    Log.i(TAG, "RECIBIDO CORRECTAMENTE")
                else Log.i(TAG, "ERROR")

            } else if (characteristic.value[0].toInt() == 85 &&
                characteristic.value[1].toInt() == 66
            ) {
                Log.i(TAG, "DIM: ${characteristic.value.size}")
                Log.i(TAG, "Pos 1: ${characteristic.value[0].toInt()} - ${characteristic.value[0].toUByte().toInt()}")
                Log.i(TAG, "Pos 2: ${characteristic.value[1].toInt()} - ${characteristic.value[1].toUByte().toInt()}")
                Log.i(TAG, "Pos 3: ${characteristic.value[2].toInt()} - ${characteristic.value[2].toUByte().toInt()}")
                Log.i(TAG, "Pos 4: ${characteristic.value[3].toInt()} - ${characteristic.value[3].toUByte().toInt()}")
                Log.i(TAG, "Pos 5: ${characteristic.value[4].toInt()} - ${characteristic.value[4].toUByte().toInt()}")
                //Log.i(TAG, "Pos 6: ${characteristic.value[5].toInt()} - ${characteristic.value[5].toUByte().toInt()}")
            } else if (characteristic.value[0].toInt() == 85 &&
                (characteristic.value[1].toInt() == 17 || characteristic.value[1].toInt() == 18 ||
                        characteristic.value[1].toInt() == 19 || characteristic.value[1].toInt() == 20 ||
                        characteristic.value[1].toInt() == 21)) {
                Log.i(TAG, "DIM: ${characteristic.value.size}")
                Log.i(TAG, "Pos 1: ${characteristic.value[0].toInt()} - ${characteristic.value[0].toUByte().toInt()}")
                Log.i(TAG, "Pos 2: ${characteristic.value[1].toInt()} - ${characteristic.value[1].toUByte().toInt()}")
                Log.i(TAG, "Pos 3: ${characteristic.value[2].toInt()} - ${characteristic.value[2].toUByte().toInt()}")
                Log.i(TAG, "Pos 4: ${characteristic.value[3].toInt()} - ${characteristic.value[3].toUByte().toInt()}")
                Log.i(TAG, "Pos 5: ${characteristic.value[4].toInt()} - ${characteristic.value[4].toUByte().toInt()}")
                //Log.i(TAG, "Pos 6: ${characteristic.value[5].toInt()} - ${characteristic.value[5].toUByte().toInt()}")
            }
            Log.i(TAG, "onCharacteristicChanged: Received")
            if (ActivityCompat.checkSelfPermission(
                    mContext,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
            }
            gatt.disconnect()
            gatt.close()
        }

    }

}