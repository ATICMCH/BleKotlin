package com.mch.blekot.services

import java.util.*
import java.io.File
import java.net.Socket
import android.util.Log
import kotlinx.coroutines.*
import java.io.FileInputStream
import java.io.DataOutputStream
import android.media.MediaPlayer
import android.media.MediaRecorder
import java.io.BufferedInputStream
import com.mch.blekot.common.Constants
import com.mch.blekot.common.JsonManager
import java.text.Format
import java.text.SimpleDateFormat

class Recorder(val path: String) {

    private var recorder: MediaRecorder? = null
    private var fileName: String? = null
    private var localPath = ""
    private var TAG = "RECORDER"

    private var isRecording = false

    fun startRecord() {

        recorder = MediaRecorder()// TODO: Esta deprecated, buscar forma correcta
        recorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        localPath = path
            localPath += if (fileName == null) {
                "/Recorder_" + UUID.randomUUID()
                    .toString() + "${Constants.ID}, ${JsonManager.getTime()}, .m4a"
            } else {
                fileName
            }
        recorder?.setOutputFile(localPath)
        recorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        try {
            recorder?.prepare()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            isRecording = false
            return
        }
        recorder?.start()
        isRecording = true
    }

    private fun reset() {
        if (recorder != null) {
            recorder?.release()
            recorder = null
            isRecording = false
        }
    }

    private fun stopRecording() {
        reset()
        try {
            Thread.sleep(150)
            recorder?.stop()
            recorder?.release()
            recorder = null
            isRecording = false

            Log.i(TAG, "path: $localPath")

            injectMedia(localPath)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /******** Para probar si se guardo bien el audio **********/
    var player: MediaPlayer? = MediaPlayer()

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
                //mediaPlayListener?.onStopMedia() todo: que hace esto
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
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