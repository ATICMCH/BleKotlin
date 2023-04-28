package com.mch.blekot.services

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

const val TAG = "RECORDER"

class Recorder(private var recorder: MediaRecorder?, private val mContext: Context) {

    private var localPath = ""
    private var isRecording = false


    //    fun startRecord() {
//        with(recorder) {
//            setOutputFile(localPath)
//            try {
//                prepare()
//                start()
//            } catch (e: Error) {
//                e.printStackTrace()
//                isRecording = false
//                return
//            }
//            isRecording = true
//        }
//    }
    private val destPath: String =
        mContext?.applicationContext?.getExternalFilesDir(null)?.absolutePath ?: ""


    fun startRecorder() {
        localPath = destPath
        localPath += "/prueba.m4a"

        Log.i(TAG, localPath)
        recorder = MediaRecorder()
        try {
            with(recorder!!) {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(localPath)
                prepare()
                start()
            }
            Log.i(TAG, "Start Recorder")
        } catch (e: Exception) {
            Log.e(TAG, "SecurityException: " + Log.getStackTraceString(e))
        }
        Thread.sleep(30000)
        stopRecording()
    }

    private fun stopRecording() {
        try {
            Thread.sleep(150)
            recorder?.stop()
            recorder?.release()//Todo: esto puede quitar la configuracion de
            isRecording = false

            Log.i(TAG, "path: $localPath")

            injectMedia(localPath)
            Log.i(TAG, "Stop Recorder")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /******** Para probar si se guardo bien el audio **********/
    private var player: MediaPlayer? = MediaPlayer()

    fun resetMediaPlayer() {
        if (player != null) {
            player!!.release()
            player = null
        }
    }

    private fun injectMedia(audioUri: String?) {
        try {
            player!!.setDataSource(audioUri)
            player!!.prepare()
            player!!.setOnCompletionListener {

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        startPlaying()
    }

    private fun startPlaying() {
        if (player != null) player!!.start()
        //mediaPlayListener?.onStartMedia()
    }

    private fun stopPlaying() {
        if (player != null && player!!.isPlaying) player!!.pause()
        //mediaPlayListener?.onStopMedia()
    }


    fun sendAudio(path: String) {
        val file = File(localPath)

        val socket = Socket()
        val out = socket.getOutputStream()
        val dataOut = DataOutputStream(out)
        dataOut.writeUTF("")

        val fis = FileInputStream(file)
        val bis = BufferedInputStream(fis)
        val mByteArray = ByteArray(file.length().toInt())
        bis.read(mByteArray, 0, mByteArray.size)
        out.write(mByteArray, 0, mByteArray.size)
        out.close()
    }
}