package com.softdev.kuleuven.thesis.helpers;

import android.app.Activity;
import android.content.Context;

import com.softdev.kuleuven.thesis.helpers.OnEventListeners.WakewordOnEventListener;

import java.io.File;

import ai.picovoice.porcupinemanager.KeywordCallback;
import ai.picovoice.porcupinemanager.PorcupineManager;

public class WakewordHelper {

    private static WakewordHelper _instance;
    private PorcupineManager porcupineManager = null;


    private WakewordHelper() {
    }

    public static WakewordHelper getInstance() {
        if (_instance == null) {
            _instance = new WakewordHelper();
        }
        return _instance;
    }

    public void process(Context context, WakewordOnEventListener mCallBack) {
        try {
            porcupineManager = initPorcupine(context, mCallBack);
            porcupineManager.start();
        } catch (Exception e) {
            mCallBack.onFailure();
        }
    }

    public void stop() {
        try {
            porcupineManager.stop();
            porcupineManager = null;
            _instance = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private PorcupineManager initPorcupine(final Context context, final WakewordOnEventListener mCallBack) throws Exception {
        //Toast.makeText(context, "init...", Toast.LENGTH_LONG).show();
        String keywordFilePath = new File(context.getFilesDir(), "blueberry.ppn").getAbsolutePath();
        String modelFilePath = new File(context.getFilesDir(), "porcupine_params.pv").getAbsolutePath();
        return new PorcupineManager(modelFilePath, keywordFilePath, 1f, new KeywordCallback() {
            @Override
            public void run() {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Log.d(TAG, "DETECTED ");
                        mCallBack.onSuccess();
                    }
                });
            }
        });
    }
}

