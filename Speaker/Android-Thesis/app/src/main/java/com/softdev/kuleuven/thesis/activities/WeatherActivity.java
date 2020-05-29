package com.softdev.kuleuven.thesis.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.softdev.kuleuven.thesis.EndActivity;
import com.softdev.kuleuven.thesis.MainActivity;
import com.softdev.kuleuven.thesis.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

/**
 *
 */

public class WeatherActivity extends EndActivity {

    private String weatherResponse;
    private String city;
    private double temp;
    private int icon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        TextView textView = findViewById(R.id.textView);
        TextView cityView = findViewById(R.id.cityView);
        TextView tempView = findViewById(R.id.tempView);
        ImageView imageView = findViewById(R.id.imageView);
        ImageView close = findViewById(R.id.imageView2);

        try {
            JSONObject response = new JSONObject((String) Objects.requireNonNull(b.get("response")));
            weatherResponse = response.getJSONObject("parameters").getJSONObject("text").getString("stringValue");
            city = response.getJSONObject("parameters").getJSONObject("city").getString("stringValue");
            temp = response.getJSONObject("parameters").getJSONObject("temp").getDouble("numberValue");
            icon = response.getJSONObject("parameters").getJSONObject("icon").getInt("numberValue");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("city",city);
        cityView.setText(city);
        Log.d("Temp", String.valueOf(temp));
        tempView.setText(temp + " °C");
        Log.d("text",weatherResponse);
        textView.setText(weatherResponse);
        String mDrawableName = "weather_" + icon;

        int drawableResourceId = this.getResources().getIdentifier(mDrawableName, "drawable", this.getPackageName());

        imageView.setImageResource(drawableResourceId);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

}
