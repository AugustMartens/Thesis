package com.softdev.kuleuven.thesis;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.softdev.kuleuven.thesis.activities.RegistrationActivity;
import com.softdev.kuleuven.thesis.activities.WifiActivity;
import com.softdev.kuleuven.thesis.helpers.ConnectionHelper;
import com.softdev.kuleuven.thesis.helpers.NetworkHelper;
import com.softdev.kuleuven.thesis.helpers.OnEventListeners.VolleyOnEventListener;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Skeleton of an Android Things activity.
 */
public class BootActivity extends Activity {

    private static final String TAG = "BootActivity";
    private  String android_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boot);

        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        try {
            if (isInternetAvailable()){
                checkIfRegistered();
            }else {
                launchWifiActivity();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            //Restarts the activity
            this.recreate();
        }
    }


    private boolean isInternetAvailable() throws InterruptedException {
        NetworkHelper networkThread = new NetworkHelper();
        Thread thread = new Thread(networkThread);
        thread.start();
        thread.join();
        return networkThread.getValue();
    }

    private void checkIfRegistered() {
        Log.d(TAG, "DeviceID: " + android_id);
        ConnectionHelper.getInstance().checkRegistration(getApplicationContext(), android_id, new VolleyOnEventListener() {
            @Override
            public void onSuccess(JSONObject res) {
                Log.d(TAG, "onSuccess");

                try {
                    if (res.getString("speakerId").equals(android_id)){
                        launchMainActivity();
                    }else {
                        launchRegisterActivity();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    launchRegisterActivity();
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "onFailure Moving to Register Activity: ", e);
                launchRegisterActivity();
            }
        });
    }

    private void launchWifiActivity(){
        Intent intent = new Intent(this, WifiActivity.class);
        startActivity(intent);
        finish();
    }

    private void launchMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void launchRegisterActivity() {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: BootActivity");
        ConnectionHelper.getInstance().onStop();
    }


}

