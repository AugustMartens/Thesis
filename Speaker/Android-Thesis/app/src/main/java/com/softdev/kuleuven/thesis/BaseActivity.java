package com.softdev.kuleuven.thesis;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.softdev.kuleuven.thesis.helpers.OnEventListeners.WakewordOnEventListener;
import com.softdev.kuleuven.thesis.helpers.WakewordHelper;
import com.softdev.kuleuven.thesis.speech.SpeechActivity;
public abstract class BaseActivity extends Activity {

    private static final String TAG = "BaseActivity";
    private static WakewordHelper wakewordHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wakewordHelper = WakewordHelper.getInstance();
        init();
    }

    void goToSpeech(){
        if (wakewordHelper != null) {
            wakewordHelper.stop();
        }
        //TODO START RECORDING
        Intent intent = new Intent(getApplicationContext(), SpeechActivity.class);
        startActivity(intent);
        finish();
    }

    private void init(){
        Log.d(TAG, "init: WakeWordHelper");
        wakewordHelper.process(this, new WakewordOnEventListener() {
            @Override
            public void onSuccess() {
                Log.i(TAG, "Detected WakeWord ");
                goToSpeech();
            }

            @Override
            public void onFailure() {
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: BaseActivity");
        wakewordHelper = null;
    }

}
