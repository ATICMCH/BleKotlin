package com.mch.blekot.services

import java.util.*
import kotlinx.coroutines.*
import android.content.Context
import android.media.MediaRecorder
import java.io.File

class Recorder {


    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)
    private lateinit var mediaRecorder: MediaRecorder
    private lateinit var audioFile: File

    private fun startRecording() = scope.launch {
        //Todo: get directory from MicroService
        val audioDir = getDir("AudioRecord", Context.MODE_PRIVATE)
        if (audioDir.exists()) {
            audioDir.deleteRecursively()
        }
        audioDir.mkdir()
        audioFile = File(audioDir, "myAudioFile.3gp")


        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(audioFile.absolutePath)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            prepare()
        }

        mediaRecorder.start()

        // Detener la grabación después de 3 minutos (180000 milisegundos)
        delay(180000)
        stopRecording()
    }

    private fun stopRecording() {
        mediaRecorder.apply {
            stop()
            release()
        }
    }

}