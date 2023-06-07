package com.mch.blekot.model.micro

import java.util.*
import android.util.Log
import kotlin.math.log10
import kotlinx.coroutines.*
import kotlin.math.roundToLong
import android.content.Context
import android.media.MediaRecorder
import com.mch.blekot.views.MainActivity
import androidx.lifecycle.Lifecycle
import android.annotation.SuppressLint
import androidx.lifecycle.LifecycleOwner

/*
* DECIBEL_DATA_LENGTH es la cantidad de registros que se almacenaran en el stack que guarda los
* db. Esto multiplicado por INTERVAL_GET_DECIBEL que es el intervalo de tiempo entre cada vez que se
* miden los db, nos dara el tiempo total de escucha y la cantidad de registros que usaremos para
* evaluar la media de ruido.
*/

const val DECIBEL_DATA_LENGTH = 4
const val INTERVAL_GET_DECIBEL = 3000L //segundos (como Francia)

const val RUIDO_MIN = 10 //Minimo de decibelios para comenzar a grabar
const val EMA_FILTER = 0.6 // No tocar


@SuppressLint("StaticFieldLeak")
object MicroService : LifecycleOwner {

    private var mEMA = 0.0
    private var decibels = 0.0
    private var continueMeasure = true
    private var mRecorder: MediaRecorder? = null
    private var decibelsHistory: Stack<Double> = Stack()

    private lateinit var mContext: Context

    fun setContext(context: Context) {
        mContext = context
    }

    fun setUpRecorder() {
        if (mRecorder == null) {
            mRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile("/dev/null")//Este path es para que no guarde lo que se graba
            }.also { recorder ->
                try {
                    recorder.prepare()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        continueMeasure = true
        launchDecibelsMeasure()
    }

    //TODO: Liberar los recursos del recorder

    private fun launchDecibelsMeasure() {
        try {
            mRecorder?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        executeAction {
            while (continueMeasure) {
                delay(INTERVAL_GET_DECIBEL)
                measureDecibels()

                if (!continueMeasure) {
                    break
                }
            }
        }
    }

    private fun measureDecibels() {
        val maxAmplitude: Double? = mRecorder?.maxAmplitude?.toDouble()
        if (maxAmplitude!! > 0 && maxAmplitude < 1000000) {
            decibels = convertDb(maxAmplitude)
            if (decibelsHistory.size >= DECIBEL_DATA_LENGTH) {
                if (decibelMedia(decibelsHistory) > RUIDO_MIN) {
                    noiseExceeded()
                }
            }
            Log.i(TAG, "push: ${decibelsHistory.push(decibels)}");
        }
    }

    private fun noiseExceeded() {
        Log.i(TAG, "turnOffLight")
        decibelsHistory.clear();
        continueMeasure = false

        executeAction { stopRecorder() }

        Recorder().run {
            startRecorder()
        }
    }

    fun stopRecorder() {
        try {
            mRecorder?.stop()
            mRecorder?.release()
            mRecorder = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun decibelMedia(decHist: Stack<Double>): Double {
        var med = 0.00
        val sizeTmp: Int = decHist.size
        while (!decHist.isEmpty()) {
            med += decHist.pop()
        }
        return (med / sizeTmp * 100.0 / 100.0).roundToLong().toDouble()
    }


    private fun executeAction(block: suspend () -> Unit): Job {
        return GlobalScope.launch(Dispatchers.IO) {
            block()
        } ?: return Job()
    }


    /*----------------------------function for take ambient decibels----------------------------*/
    private fun convertDb(amplitude: Double): Double {
        /*
         *Los móviles pueden alcanzar hasta 90 db + -
         *getMaxAmplitude devuelve un valor entre 0 y 32767 (en la mayoría de los teléfonos). eso
         *significa que si el db máximo es 90, la presión en el micrófono es 0.6325 Pascal. Se hace una
         *comparación con el valor anterior de getMaxAmplitude. necesitamos dividir maxAmplitude con
         *(32767/0.6325) 51805.5336 o si son 100db entonces 46676.6381
         */

        mEMA = EMA_FILTER * amplitude + (1.0 - EMA_FILTER) * mEMA

        /*
        *Asumiendo que la presión de referencia mínima es 0.000085 Pascal
        *(en la mayoría de los teléfonos) es igual a 0 db
        *return 20 * (float) Math.log10((mEMAValue / 51805.5336) / 0.000028251);
        */

        return (20 * log10(mEMA / 51805.5336 / 0.000028251) * 100).roundToLong() / 100.0
    }

    override fun getLifecycle(): Lifecycle {
        return MainActivity.getInstance()!!.lifecycle
    }
}




//    private var runner: Thread? = Thread {
//        while (continueMeasure) {
//            try {
//                Thread.sleep(INTERVAL_GET_DECIBEL)
//                measureDecibels()
//            } catch (e: InterruptedException) {
//                e.printStackTrace()
//            }
//        }
//    }

//    private fun startMeasure() {
//        runBlocking {
//            while (continueMeasure) {
//                try {
//                    delay(INTERVAL_GET_DECIBEL)
//                    measureDecibels()
//                } catch (e: InterruptedException) {
//                    e.printStackTrace()
//                }
//        }
//    }
