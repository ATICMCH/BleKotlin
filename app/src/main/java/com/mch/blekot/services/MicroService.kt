package com.mch.blekot.services

import java.util.*

import android.util.Log
import android.os.Build
import kotlin.math.log10
import android.os.IBinder
import android.app.Service
import kotlinx.coroutines.*
import android.content.Intent
import kotlin.math.roundToLong
import android.content.Context
import android.media.MediaRecorder
import androidx.annotation.RequiresApi


class MicroService : Service() {

    private var mRecorder: MediaRecorder? = null
    private var decibels = 0.0
    private val TAG = "MicroService"
    private var mEMA = 0.0

    private var decibelsHistory: Stack<Double> = Stack()


    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate() {
        Log.d(TAG, "Servicio micro...")
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Servicio iniciado...")


        return START_NOT_STICKY
    }

    /*----------------------------function for take ambient decibels----------------------------*/
    private fun convertDb(amplitude: Double): Double {
        /*
         *Los teléfonos celulares pueden alcanzar hasta 90 db + -
         *getMaxAmplitude devuelve un valor entre 0 y 32767 (en la mayoría de los teléfonos). eso
         *significa que si el db máximo es 90, la presión en el micrófono es 0.6325 Pascal. hace una
         *comparación con el valor anterior de getMaxAmplitude. necesitamos dividir maxAmplitude con
         *(32767/0.6325) 51805.5336 o si 100db entonces 46676.6381
         */
        val EMA_FILTER = 0.6

        /*---------------Decibels measure---------------*/
        //static final private double EMA_FILTER = 0.6;

        mEMA = EMA_FILTER * amplitude + (1.0 - EMA_FILTER) * mEMA
        //Asumiendo que la presión de referencia mínima es 0.000085 Pascal
        // (en la mayoría de los teléfonos) es igual a 0 db
        // TODO: Find out the minimum reference in Redmi
        // return 20 * (float) Math.log10((mEMAValue / 51805.5336) / 0.000028251);
        return (20 * log10(mEMA / 51805.5336 / 0.000028251) * 100).roundToLong() / 100.0
    }

    private fun measureDecibels() {
        val amplitude: Double? = mRecorder?.maxAmplitude?.toDouble()
        //Log.i("ron11", "ron11: " + String.format(Locale.US,"%.2f", amplitude));
        if (amplitude!! > 0 && amplitude < 1000000) {
            decibels = convertDb(amplitude)
            Log.i(TAG, "Decibels: $decibels")

            /*
            if (true) { // decibels > Constants.RUIDO_MIN
                if (decibelsHistory.size >= Constants.DECIBEL_DATA_LENGTH) {
                    //count = 0;
                    if (decibelMedia(decibelsHistory) > Constants.RUIDO_MIN) {
                        Ewelink.turnOffLight()
                        Log.i("dec", "turnOffLight: Aqui se apago la luz de sus ojos")
                        //decibelsHistory.clear();
                    }
                }
                //count++;
                //Log.i("dec", "push-"+count+": " + decibelsHistory.push(decibels));
            }

             */
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

}