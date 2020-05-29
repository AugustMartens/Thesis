/*
 * August Martens SpeechActivity
*/

package com.softdev.kuleuven.thesis.speech;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.softdev.kuleuven.thesis.MainActivity;
import com.softdev.kuleuven.thesis.R;
import com.softdev.kuleuven.thesis.activities.FailedActivity;
import com.softdev.kuleuven.thesis.activities.GenericActivity;
import com.softdev.kuleuven.thesis.activities.MapsActivity;
import com.softdev.kuleuven.thesis.activities.MusicActivity;
import com.softdev.kuleuven.thesis.activities.NewsActivity;
import com.softdev.kuleuven.thesis.activities.WeatherActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static java.lang.Boolean.TRUE;


public class SpeechActivity extends Activity {

    private static final String FRAGMENT_MESSAGE_DIALOG = "message_dialog";

    private static final String STATE_RESULTS = "results";

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 1;
    private static final String TAG = "MainActivity";

    private SpeechService mSpeechService;

    private JSONObject response;
    private String android_id;
    private DownloadTask downloadTask;
    private Intent i = null;

    private VoiceRecorder mVoiceRecorder;
    private final VoiceRecorder.Callback mVoiceCallback = new VoiceRecorder.Callback() {

        @Override
        public void onVoiceStart() {
            showStatus();
            if (mSpeechService != null) {
                mSpeechService.startRecognizing(mVoiceRecorder.getSampleRate());
            }
        }

        @Override
        public void onVoice(byte[] data, int size) {
            if (mSpeechService != null) {
                mSpeechService.recognize(data, size);
            }
        }

        @Override
        public void onVoiceEnd() {
            showStatus();
            if (mSpeechService != null) {
                mSpeechService.finishRecognizing();
            }
        }

    };

    // Resource caches
    private int mColorHearing;
    private int mColorNotHearing;

    private TextView mText;

    //private ResultAdapter mAdapter;
    //private RecyclerView mRecyclerView;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            mSpeechService = SpeechService.from(binder);
            mSpeechService.addListener(mSpeechServiceListener);
            //mStatus.setVisibility(View.VISIBLE);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mSpeechService = null;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Resources resources = getResources();
        final Resources.Theme theme = getTheme();

        mText = findViewById(R.id.text);
        // View references
        TextView timeView = findViewById(R.id.textView);

        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        Date time = Calendar.getInstance().getTime();
        SimpleDateFormat outputFmt = new SimpleDateFormat("HH:mm");
        outputFmt.setTimeZone(TimeZone.getTimeZone("Europe/Brussels"));
        timeView.setText(outputFmt.format(time));

    }

    @Override
    protected void onStart() {
        super.onStart();

        // Prepare Cloud Speech API
        bindService(new Intent(this, SpeechService.class), mServiceConnection, BIND_AUTO_CREATE);

        // Start listening to voices
        startVoiceRecorder();
    }

    @Override
    protected void onStop() {
        // Stop listening to voice
        stopService();

        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (permissions.length == 1 && grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startVoiceRecorder();
            } else {
                Log.d(TAG, "onRequestPermissionsResult: no permission");
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    private void startVoiceRecorder() {
        if (mVoiceRecorder != null) {
            mVoiceRecorder.stop();
        }
        mVoiceRecorder = new VoiceRecorder(mVoiceCallback);
        mVoiceRecorder.start();
    }

    private void stopVoiceRecorder() {
        if (mVoiceRecorder != null) {
            mVoiceRecorder.stop();
            mVoiceRecorder = null;
        }
    }


    private void showStatus() {

    }


    private final SpeechService.Listener mSpeechServiceListener =
            new SpeechService.Listener() {
                @Override
                public void onSpeechRecognized(final String text, final boolean isFinal) {
                    if (isFinal) {
                        mVoiceRecorder.dismiss();
                    }
                    if (mText != null && !TextUtils.isEmpty(text)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (isFinal) {
                                    mText.setText(text);
                                    mText.setTextColor(Color.GREEN);
                                    execute(text);
                                } else {
                                    mText.setText(text);
                                }
                            }
                        });
                    }
                }
            };

    /// TOEVOEGINGEN

    private void stopService() {
        // Stop listening to voice
        stopVoiceRecorder();

        // Stop Cloud Speech API
        if (mSpeechService != null) {
            mSpeechService.removeListener(mSpeechServiceListener);
            unbindService(mServiceConnection);
            mSpeechService = null;
        }
    }

    private void launch(JSONObject intent) throws JSONException {
        boolean success = true;
        Log.i(TAG, "intentID" + intent.getInt("intentId"));
        switch (intent.getInt("intentId")) {
            case 0:
                Log.i("Launch:", "No intent Mached");
                i = new Intent(this, FailedActivity.class);
                break;
            case 100:
                Log.i("Launch:", "Weather");
                i = new Intent(this, WeatherActivity.class);
                break;
            case 118:
                Log.i("Launch:", "News");
                i = new Intent(this, NewsActivity.class);
                break;
            case 105:
                Log.i("Launch:", "Directions");
                i = new Intent(this, MapsActivity.class);
                break;
            case 200:
                Log.i("Launch:", "Spotify");
                i = new Intent(this, MusicActivity.class);
                break;
            default:
                Log.i("Launch:", "Generic");
                i = new Intent(this, GenericActivity.class);
        }
        stopVoiceRecorder();
        stopService();
        writeToFile(intent.getString("audio"),getApplicationContext());
        intent.remove("audio");
        i.putExtra("response", intent.toString());
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        toNewActivity();
    }

    private void error() {
        Log.d(TAG, "error: ");
        stopVoiceRecorder();
        stopService();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    private void toNewActivity(){
        Log.d(TAG, "toNewActivity: In new Activity");
        if (i != null) {
            startActivity(i);
            finish();
        }else{
            Log.d(TAG, "toNewActivity:  i was null");
        }
    }

    private void writeToFile(String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("speech.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }


    public void playAudio(String base64EncodedString) {
        try {
            String url = "data:audio/mp3;base64," + base64EncodedString;
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(url);
            mediaPlayer.setVolume(1.0f, 1.0f);
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(
                    new MediaPlayer.OnCompletionListener() {

                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mp.release();
                            toNewActivity();
                        }

                    });
            mediaPlayer.start();
        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }
    }

    private void execute(String query){
        String toSend = "{\"id\":\""+android_id+"\",\"text\":\""+query+"\"}";
        downloadTask = new DownloadTask();
        downloadTask.execute(toSend);
    }

    private void onResult(JSONObject response){
        Log.d(TAG, "onResult() called with: response = [" + response + "]");
        try {
            launch(response);
        } catch (Exception e) {
            Log.e(TAG, "onResult: Error", e);
            error();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"SpeechActivity Destroyed");
        stopService();
        stopVoiceRecorder();
        downloadTask.cancel(TRUE);
    }

    class DownloadTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... varargs) {
            try {

                //Change the URL with any other publicly accessible POST resource, which accepts JSON request body
                String GATEWAY_URL = "https://europe-west1-thesis-c30c3.cloudfunctions.net/dialogflowGateway/";
                URL url = new URL(GATEWAY_URL);

                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("POST");

                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                con.setDoOutput(true);

                OutputStream os = con.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);
                osw.write(varargs[0]);
                osw.flush();
                osw.close();
                os.close();  //don't forget to close the OutputStream
                con.connect();


                int code = con.getResponseCode();
                Log.i(TAG, "ResponseCode: "+ code);

                try(BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))){
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    Log.i(TAG, "Response: " + response.toString());
                    return new JSONObject(response.toString());
                }
            }catch (Exception e){
                Log.e(TAG, "ERROR: ",e );
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject response){
            onResult(response);
        }

    }

    //    private void launch(int intentId) throws JSONException {
//        Intent i;
//        switch (intentId) {
//            case 100:
//                // Weer
//                i = new Intent(this, WeatherActivity.class);
//                i.putExtra("sessionId", response.getString("sessionId"));
//                i.putExtra("keepAlive", response.getBoolean("keepAlive"));
//                i.putExtra("intentId", response.getInt("intentId"));
//                i.putExtra("intentName", response.getString("intentName"));
//                i.putExtra("text", response.getString("intentName"));
//
//
//                //Audio
//
//                playAudio(response.getString("audio"));
//
//                //Parameters
//
//                JSONObject parameters = response.getJSONObject("parameters");
//
//                Log.d("Cast", "In Launch parms :" + parameters);
//
//                i.putExtra("weatherResponse", parameters.getJSONObject("text").getString("stringValue"));
//
//                Log.d("Cast", "In Launch parms :" + parameters.getJSONObject("text").getString("stringValue"));
//
//                i.putExtra("city", parameters.getJSONObject("city").getString("stringValue"));
//                i.putExtra("temp", parameters.getJSONObject("temp").getInt("numberValue"));
//                i.putExtra("icon", parameters.getJSONObject("icon").getInt("numberValue"));
//
//                //Start Activity
//                Log.d("Cast", "Cast succesfull");
//
//                startActivity(i);
//                break;
//            case 200:
//                // code block
//                break;
//            default:
//                mText.setText("Sorry ik heb het niet begrepen..");
//                mText.setTextColor(Color.RED);
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                i = new Intent(this, MainActivity.class);
//                startActivity(i);
//
//        }
//
//    }

}
