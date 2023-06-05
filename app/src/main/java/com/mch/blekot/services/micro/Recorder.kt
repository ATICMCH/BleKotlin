package com.mch.blekot.services.micro

import java.io.File
import android.util.Log
import org.json.JSONObject
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.media.MediaRecorder
import com.mch.blekot.common.getTime
import kotlinx.coroutines.runBlocking
import com.mch.blekot.common.Constants
import com.mch.blekot.model.socket.SocketSingleton

const val TAG = "RECORDER"

class Recorder {

    private var localPath = ""
    private var isRecording = false
    private var recorder: MediaRecorder? = null

    fun startRecorder() {
        localPath = Constants.destPath
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
            recorder?.stop()
            recorder?.release()
            recorder = null
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
        restartMicroService()
    }

    private fun restartMicroService(){
        MicroService.setUpRecorder()
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
