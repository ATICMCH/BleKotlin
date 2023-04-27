package com.mch.blekot.services;

import java.util.Stack;
import android.util.Log;
import android.os.IBinder;
import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import androidx.annotation.Nullable;

import com.mch.blekot.R;

public class MicroService extends Service {

    double mAmplitude, decibels;
    MediaRecorder mRecorder = new MediaRecorder();
    Stack<Double> decibelsHistory = new Stack<>();

    @Override
    public void onCreate() {
        super.onCreate();
        startMicro();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        measureDecibels();
        return null;
    }

    private void startRecorder(){
        String destPath = getApplicationContext().getExternalFilesDir(null).getAbsolutePath();
        Recorder recorder = new Recorder(destPath);
        recorder.startRecord();
    }

    public void startMicro(){
        if (mRecorder == null) {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile("/dev/null");
            try {
                mRecorder.prepare();
            } catch (java.io.IOException | java.lang.SecurityException e) {
                android.util.Log.e("[Micro]", "IOException: " +
                        android.util.Log.getStackTraceString(e));
            }
            try {
                mRecorder.start();
            } catch (java.lang.SecurityException e) {
                android.util.Log.e("[Micro]", "SecurityException: " +
                        android.util.Log.getStackTraceString(e));
            }
        }
    }

    public void measureDecibels() {
        double amplitude = mRecorder.getMaxAmplitude();
        if (amplitude > 0 && amplitude < 1000000) {
            decibels = convertDb(amplitude);
            if (decibels > 10) {
                decibels = convertDb(mRecorder.getMaxAmplitude());
                if (decibelsHistory.size() >= 12) {
                    if (decibelMedia(decibelsHistory) > 10) {
                        Log.i("dec", "turnOffLight: Aqui se apago la luz de sus ojos");
                        decibelsHistory.clear();
                    }
                    Log.i("dec", "pop: " + decibelsHistory.remove(0));
                }
                Log.i("dec", "push: " + decibelsHistory.push(decibels));
            }
        }
    }

    /*----------------------------function for take ambient decibels----------------------------*/
    public double convertDb(double amplitude) {
        /*
         *Los teléfonos pueden alcanzar hasta 90 db + -
         *getMaxAmplitude devuelve un valor entre 0 y 32767 (en la mayoría de los teléfonos). eso
         *significa que si el db máximo es 90, la presión en el micrófono es 0.6325 Pascal. se hace una
         *comparación con el valor anterior de getMaxAmplitude. necesitamos dividir maxAmplitude con
         *(32767/0.6325) 51805.5336 o si son 100db la max entonces 46676.6381
         */
        double EMA_FILTER = 0.6;
        /*SharedPreferences sp = this.getSharedPreferences("device-base", MODE_PRIVATE);
         *double amp = (double) sp.getFloat("amplitude", 0);
         */
        double mEMA = 0.0;
        double mEMAValue = EMA_FILTER * amplitude + (1.0 - EMA_FILTER) * mEMA;
        //Asumiendo que la presión de referencia mínima es 0.000085 Pascal
        // (en la mayoría de los teléfonos) es igual a 0 db
        return 20 * (float) Math.log10((mEMAValue / 51805.5336) / 0.000028251);
    }

    private double decibelMedia(Stack<Double> decHist) {
        Double med = 0D;
        for (int i = 0; i < decHist.size(); i++) {
            med += decHist.get(i);
            Log.i("dec", "media: " + med);
        }
        Log.i("dec", "decibelMedia: " + med / decHist.size());
        return Math.round(((med / decHist.size()) * 100d) / 100d);
    }
}