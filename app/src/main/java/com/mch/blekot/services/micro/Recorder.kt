package com.mch.blekot.services.micro

import android.content.Context
import java.io.File
import java.net.Socket
import android.util.Log
import java.io.FileInputStream
import java.io.DataOutputStream
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.CountDownTimer
import java.io.BufferedInputStream
import com.mch.blekot.common.Constants
import com.mch.blekot.common.JsonManager
import com.mch.blekot.common.getTime
import com.mch.blekot.model.socket.SocketSingleton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONObject

const val TAG = "RECORDER"

class Recorder( mContext: Context) {

    private var localPath = ""
    private var isRecording = false
    private lateinit var recorder: MediaRecorder

    //Tomamos el path del almacenamiento de Android.
    private val destPath: String =
        mContext.applicationContext?.getExternalFilesDir(null)?.absolutePath ?: ""

    fun startRecorder() {
        localPath = destPath
        localPath += "/${Constants.ID}_${getTime()}.m4a"
        Log.i(TAG, localPath)

        runBlocking {
            val job = launch{
                recorder = MediaRecorder().apply {
                    try {
                        setAudioSource(MediaRecorder.AudioSource.MIC)
                        setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                        setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                        setOutputFile(localPath)
                        prepare()
                        start()
                        Log.i(TAG, "Start Recorder")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            delay(10000)
            while (job.isActive){
                Log.i(TAG, "recording")
            }
            if(job.isCompleted){
                stopRecording()
            }
        }

    }

    private fun stopRecording() {
        try {
            recorder.stop()
            recorder.release()
            isRecording = false

            Log.i(TAG, "path: $localPath")

            sendAudio(File(localPath))

            //Para reproducir el audio
            //injectMedia(localPath)
            Log.i(TAG, "Stop Recorder")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun sendAudio(file: File) {

        Log.i(TAG, "sendAudio")

        JSONObject().apply {
            put("name", file.name)
            put("file", file.readBytes())
        }.also { audioFile ->
            SocketSingleton.socketInstance?.socket?.emit("file", Constants.ID, audioFile)
        }
    }
}

    /**Para probar el audio solo hay que pasarle el path donde fue guardado a injectMediaz

    private var player: MediaPlayer? = MediaPlayer()

    private fun injectMedia(audioUri: String?) {
    try {
    player!!.setDataSource(audioUri)
    player!!.prepare()
    } catch (e: Exception) {
    e.printStackTrace()
    }
    //startPlaying()
    }

    private fun startPlaying() {
    if (player != null) player!!.start()
    //mediaPlayListener?.onStartMedia()
    }

    private fun stopPlaying() {
    if (player != null && player!!.isPlaying) player!!.pause()
    //mediaPlayListener?.onStopMedia()
    }

    fun resetMediaPlayer() {
    if (player != null) {
    player!!.release()
    player = null
    }
    }

     */
