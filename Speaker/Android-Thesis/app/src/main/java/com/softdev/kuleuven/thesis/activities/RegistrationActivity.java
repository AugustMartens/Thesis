package com.softdev.kuleuven.thesis.activities;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.softdev.kuleuven.thesis.helpers.ConnectionHelper;
import com.softdev.kuleuven.thesis.helpers.OnEventListeners.VolleyOnEventListener;
import com.softdev.kuleuven.thesis.MainActivity;
import com.softdev.kuleuven.thesis.R;

import net.glxn.qrgen.android.QRCode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static android.content.ContentValues.TAG;

public class RegistrationActivity extends Activity {

    private  String android_id;
    private ConstraintLayout container;
    private AnimationDrawable anim;

    private final Handler handler = new Handler();
    private Runnable runnable;
    private final int delay = 15*1000;
    private MediaPlayer bgAudioPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        container = findViewById(R.id.container);

        anim = (AnimationDrawable) container.getBackground();
        anim.setEnterFadeDuration(6000);
        anim.setExitFadeDuration(2000);

        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        String website = "https://thesis-c30c3.firebaseapp.com/register?id=" + android_id;
        Bitmap myBitmap = QRCode.from(website).withSize(250, 250).bitmap();
        ImageView myImage = findViewById(R.id.qrView);
        myImage.setImageBitmap(myBitmap);

        bgAudioPlayer = MediaPlayer.create(this, R.raw.bg_audio);

        try {
            copyPorcupineResourceFiles();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed( runnable = new Runnable() {
            public void run() {
                checkIfRegistered();
                handler.postDelayed(runnable, delay);
            }
        }, delay);

        bgAudioPlayer.start();
        bgAudioPlayer.setOnCompletionListener(onCompletionListener);

        if (anim != null && !anim.isRunning())
            anim.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable); //stop handler when activity not visible
        if (bgAudioPlayer != null) {
            bgAudioPlayer.release();
        }
        if (anim != null && anim.isRunning())
            anim.stop();
    }

    private void checkIfRegistered() {
        Log.i(TAG, "checkIfRegistered!!");
        Log.d(TAG, "DeviceID: " + android_id);
        ConnectionHelper.getInstance().checkRegistration(getApplicationContext(), android_id, new VolleyOnEventListener() {
            @Override
            public void onSuccess(JSONObject res) {
                Log.d(TAG, "onSuccess" + res.toString());

                try {
                    if (res.getString("speakerId").equals(android_id)){
                        bgAudioPlayer.release();
                        bgAudioPlayer = null;
                        handler.removeCallbacks(runnable); //stop handler when activity not visible
                        launchMainActivity();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "onFailure Moving to Register Activity: ", e);

            }
        });
    }

    private void launchMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

    }

    private final MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            bgAudioPlayer.release();
            bgAudioPlayer = null;
            bgAudioPlayer = MediaPlayer.create(getApplicationContext(), R.raw.bg_audio);
            bgAudioPlayer.start();
        }
    };

    private void copyResourceFile(int resourceID, String filename) throws IOException {
        Resources resources = getResources();
        try (InputStream is = new BufferedInputStream(resources.openRawResource(resourceID), 256); OutputStream os = new BufferedOutputStream(openFileOutput(filename, Context.MODE_PRIVATE), 256)) {
            int r;
            while ((r = is.read()) != -1) {
                os.write(r);
            }
            os.flush();
        }
    }

    private static final int[] KEYWORD_FILE_RESOURCE_IDS = {
            R.raw.americano, R.raw.blueberry, R.raw.bumblebee, R.raw.grapefruit,
            R.raw.grasshopper, R.raw.picovoice, R.raw.porcupine, R.raw.terminator,
    };

    private void copyPorcupineResourceFiles() throws IOException {
        Resources resources = getResources();

        for (int keywordFileResourceID : KEYWORD_FILE_RESOURCE_IDS) {
            copyResourceFile(keywordFileResourceID, resources.getResourceEntryName(keywordFileResourceID) + ".ppn");
        }

        copyResourceFile(R.raw.porcupine_params, resources.getResourceEntryName(R.raw.porcupine_params) + ".pv");
    }

}
