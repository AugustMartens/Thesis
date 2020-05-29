package com.softdev.kuleuven.thesis.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerView;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerViewManager;
import com.softdev.kuleuven.thesis.EndActivity;
import com.softdev.kuleuven.thesis.MainActivity;
import com.softdev.kuleuven.thesis.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;


public class DirectionsActivity extends EndActivity {

    private MapView mapView;

    private MarkerViewManager markerViewManager;

    private String distance;
    private double startLat;
    private double startLng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            JSONObject response = new JSONObject((String) Objects.requireNonNull(b.get("response")));
            distance = response.getJSONObject("parameters").getJSONObject("distance").getString("stringValue");
            startLat = response.getJSONObject("parameters").getJSONObject("startLat").getDouble("numberValue");
            startLng = response.getJSONObject("parameters").getJSONObject("startLng").getDouble("numberValue");
            double endLat = response.getJSONObject("parameters").getJSONObject("endLat").getDouble("numberValue");
            double endLng = response.getJSONObject("parameters").getJSONObject("endLng").getDouble("numberValue");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //
        Mapbox.getInstance(this, "pk.eyJ1IjoibGV1dmJpa2UiLCJhIjoiY2p1d3Q1MmZ1MDB0cjN5bXhqNjExNDF3YyJ9.fuhacRD1FBodqyR0ig-GuQ");
        setContentView(R.layout.activity_directions);
        //mapView = findViewById(R.id.mapView);
        ImageView close = findViewById(R.id.imageView3);
        TextView distanceView = findViewById(R.id.distanceView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull final MapboxMap mapboxMap) {
                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        markerViewManager = new MarkerViewManager(mapView, mapboxMap);
                        MarkerView markerView = new MarkerView(new LatLng(startLat,startLng), mapView);
                        markerViewManager.addMarker(markerView);
                        // Map is set up and the style has loaded. Now you can add data or make other map adjustments.

                    }
                });
            }
        });
        //

        distanceView.setText(distance);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        markerViewManager.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

}
