package com.softdev.kuleuven.thesis.activities;

import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.softdev.kuleuven.thesis.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private Intent iin;
    private Bundle b;
    private MediaPlayer mediaPlayer;

    private double startLat;
    private double startLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        Objects.requireNonNull(mapFragment).getMapAsync(this);

        try {
            JSONObject response = new JSONObject((String) Objects.requireNonNull(b.get("response")));
            String distance = response.getJSONObject("parameters").getJSONObject("distance").getString("stringValue");
            startLat = response.getJSONObject("parameters").getJSONObject("startLat").getDouble("numberValue");
            startLng = response.getJSONObject("parameters").getJSONObject("startLng").getDouble("numberValue");
            double endLat = response.getJSONObject("parameters").getJSONObject("endLat").getDouble("numberValue");
            double endLng = response.getJSONObject("parameters").getJSONObject("endLng").getDouble("numberValue");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        iin= getIntent();
        b = iin.getExtras();

        playAudio(readFromFile(getApplicationContext()));

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        // Add a marker in Sydney and move the camera
        LatLng begin = new LatLng(startLat, startLng);
        LatLng end = new LatLng(startLat, startLng);
        googleMap.addMarker(new MarkerOptions().position(begin).title("Begin Location"));
        googleMap.addMarker(new MarkerOptions().position(begin).title("End Location"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(begin));
    }

    private String readFromFile(Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("speech.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append("\n").append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    private void playAudio(String base64EncodedString) {
        try {
            String url = "data:audio/mp3;base64," + base64EncodedString;
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(url);
            mediaPlayer.setVolume(1.0f, 1.0f);
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(
                    new MediaPlayer.OnCompletionListener() {

                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mp.release();
                        }

                    });
            mediaPlayer.start();
        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }
    }
}
