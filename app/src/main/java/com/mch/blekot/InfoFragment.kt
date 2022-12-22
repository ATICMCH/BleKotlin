package com.mch.blekot

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.mch.blekot.util.PreferencesManager

class InfoFragment : Fragment() {

    private lateinit var txtDeviceId: TextView
    private lateinit var txtSocketId: TextView
    private lateinit var txtBleName: TextView
    private lateinit var txtMacAddress: TextView

    private val TAG = "shared preferences"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_info, container, false)
        val btnSave = view.findViewById<Button>(R.id.btnSave)
        btnSave.setOnClickListener { setSharedPref() }

        txtDeviceId = view.findViewById(R.id.device_id)
        txtSocketId = view.findViewById(R.id.socket_id)
        txtBleName = view.findViewById(R.id.BLE_name)
        txtMacAddress = view.findViewById(R.id.mac_address)

        return view
    }

    private fun setSharedPref() {

        val deviceId = txtDeviceId.text.toString().trim()
        val socketId = txtSocketId.text.toString().trim()
        val bleName = txtBleName.text.toString().trim()
        val macAddress = txtMacAddress.text.toString().trim()

        PreferencesManager.setContext(requireContext())

        with(PreferencesManager) {
            setString(getString(R.string.device_id), deviceId)
            setString(getString(R.string.socket_id), socketId)
            setString(getString(R.string.ble_name), bleName)
            setString(getString(R.string.mac_address), macAddress)
            setBoolean(getString(R.string.first_time), true)
        }

        if (PreferencesManager.getBoolean(getString(R.string.first_time))) {
            Toast.makeText(
                activity, """
                DeviceID: ${PreferencesManager.getString((getString(R.string.device_id)))}
                SocketID: ${PreferencesManager.getString((getString(R.string.socket_id)))}
                BLE name: ${PreferencesManager.getString((getString(R.string.ble_name)))}
                MAC ADDRESS: ${PreferencesManager.getString((getString(R.string.mac_address)))}
            """.trimIndent(),
                Toast.LENGTH_LONG
            ).show()

            //Para que el WELCOME vuelva a ser visible
            val txt = activity?.findViewById<TextView>(R.id.textView)
            txt?.visibility = View.VISIBLE
            fragmentManager?.beginTransaction()?.remove(this)?.commit();
        }
    }
}

