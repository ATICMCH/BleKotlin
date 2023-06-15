package com.mch.blekot.views

import android.util.Log
import android.os.Bundle
import android.view.View
import android.content.Intent
import android.content.Context
import kotlinx.coroutines.launch
import android.view.WindowManager
import kotlinx.coroutines.MainScope
import android.content.IntentFilter
import android.Manifest.permission.*
import com.mch.blekot.model.Interactor
import com.mch.blekot.common.Constants
import android.content.BroadcastReceiver
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import com.mch.blekot.model.socket.SocketService
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginLeft
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.vmadalin.easypermissions.EasyPermissions
import com.mch.blekot.databinding.ActivityMainBinding
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN
import com.mch.blekot.R
import com.mch.blekot.model.socket.SocketSingleton
import com.mch.blekot.model.micro.MicroService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first

/****
 * Project: BleKot
 * From: com.mch.blekot
 * Created by: JazBass on 22/05/2023
 * Find me in github: https://github.com/JazBass
 * All rights reserved 2023
 ****/

private const val ACTION_RUN_SERVICE = "com.mch.blekot.services.action.RUN_SERVICE"
private const val ACTION_MEMORY_EXIT = "com.mch.blekot.services.action.MEMORY_EXIT"
private const val CODE_REQUEST_PERMISSIONS = 1

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding

    private val fragment = InfoFragment()
    private lateinit var microService: MicroService

    //Singleton
    private val Context.dataStore by preferencesDataStore(name = "DEVICE_ID")

    override
    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        /*
        * Tomamos el path para almacenar los audios que grabaremos y lo almacenamos en Constants,
        * lo hacemos aquí ya que es más fácil acceder al context de la app
        */
        Constants.destPath = applicationContext?.getExternalFilesDir(null)?.absolutePath ?: ""

        methodRequiresPermissions()

        mBinding.fab.setOnClickListener { launchInfoFragment() }
        mBinding.cancelFab.setOnClickListener { onBackPressed() }

        //onCLickListeners
        setUpListeners()
        askForDeviceID()

    }


    private fun askForDeviceID() {
        executeAction {
            if (!readDeviceID().isNullOrEmpty()) {
                Constants.ID = readDeviceID().toString()
                launchSocketService()
            } else {
                launchAlertDialog()
            }
        }
    }

    private fun launchAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Insterte el ID del dispositivo")
        val editText = EditText(this)

        builder.setView(editText)
        builder.setPositiveButton("OK") { _, _ ->
            val deviceId = editText.text.toString()
            executeAction {
                saveDeviceID(deviceId)
            }
        }
        builder.show()
    }

    private fun setUpListeners() {
        with(mBinding) {
            fab.setOnClickListener { launchInfoFragment() }
            cancelFab.setOnClickListener { onBackPressed() }
        }
    }

    private suspend fun saveDeviceID(id: String) {
        val deviceIdKey = stringPreferencesKey(Constants.DEVICE_ID_KEY)
        dataStore.edit { preferences ->
            preferences[deviceIdKey] = id
        }
        readDeviceID().also {
            if (!it.isNullOrEmpty()) {
                Constants.ID = it
                Log.i("DeviceID", it)
                launchSocketService()
            }
        }
    }

    private suspend fun readDeviceID(): String? {
        val deviceIdKey = stringPreferencesKey(Constants.DEVICE_ID_KEY)
        dataStore.data.first().also { preferences ->
            return preferences[deviceIdKey]
        }
    }

    private fun launchInfoFragment() {

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        with(fragmentTransaction) {
            add(R.id.containerMain, fragment, "currentFragment")
            addToBackStack(null)
            setTransition(TRANSIT_FRAGMENT_OPEN)
            commit()
        }
        mBinding.fab.visibility = View.GONE
        mBinding.cancelFab.visibility = View.VISIBLE
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        with(mBinding) {
            cancelFab.visibility = View.GONE
            fab.visibility = View.VISIBLE
        }
    }

    private fun launchSocketService() {
        val filter = IntentFilter(ACTION_RUN_SERVICE)
        filter.addAction(ACTION_MEMORY_EXIT)
        val receiver = ResponseReceiver()
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter)

        val intent = Intent(applicationContext, SocketService::class.java)
        startService(intent)
    }

    fun launchMicro() {
        if (SocketSingleton.socketInstance!!.isConnected) {
            microService = MicroService.apply {
                setContext(applicationContext)
                setUpRecorder()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    //Función que pide los permisos al iniciarse la app
    private fun methodRequiresPermissions() {
        if (!EasyPermissions.hasPermissions(
                this,
                ACCESS_FINE_LOCATION,
                RECORD_AUDIO,
                WRITE_EXTERNAL_STORAGE
            )
        ) {
            EasyPermissions.requestPermissions(
                host = this,
                rationale = getString(R.string.ACCEPT_PERMISSIONS),
                requestCode = CODE_REQUEST_PERMISSIONS,
                perms = arrayOf(ACCESS_FINE_LOCATION, RECORD_AUDIO, WRITE_EXTERNAL_STORAGE)
            )
        }
    }

    /*---------------------write char---------------------*/

    private class ResponseReceiver : BroadcastReceiver() {

        // Filtro de acciones que serán alertadas
        val ACTION_RUN_SERVICE = "com.rdajila.tandroidsocketio.services.action.RUN_SERVICE"
        val ACTION_MEMORY_EXIT = "com.rdajila.tandroidsocketio.services.action.MEMORY_EXIT"
        override fun onReceive(context: Context?, intent: Intent) {
            when (intent.action) {
                ACTION_RUN_SERVICE -> {
                    Log.d("TAG", "Servicio iniciado escucha desde MainActivity...")
                    Log.d("TAG", "Main-MSG: " + intent.getStringExtra(Constants.EXTRA_MSG))
                    Log.d("TAG", "Main-Counter: " + intent.getStringExtra(Constants.EXTRA_COUNTER))
                }
                ACTION_MEMORY_EXIT -> {
                    Log.d("TAG", "Servicio finalizado escucha desde MainActivity...")
                }
            }
        }
    }

    private fun executeAction(block: suspend () -> Unit): Job {
        return MainScope().launch { block() }
    }

    init {
        instance = this
    }

    companion object {
        private var instance: MainActivity? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }

        fun getInstance() = instance
    }

}