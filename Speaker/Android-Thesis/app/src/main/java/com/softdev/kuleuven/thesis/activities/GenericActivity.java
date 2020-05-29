package com.softdev.kuleuven.thesis.activities;


import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.softdev.kuleuven.thesis.EndActivity;
import com.softdev.kuleuven.thesis.MainActivity;
import com.softdev.kuleuven.thesis.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

/**
 * Skeleton of an Android Things activity.
 */
public class GenericActivity extends EndActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic);

        //Intent iin= getIntent();
        //Bundle b = iin.getExtras();
        TextView title = findViewById(R.id.IntentTextView);
        TextView text = findViewById(R.id.fullTextView);
        ImageView close = findViewById(R.id.imageView2);


        ConstraintLayout container = findViewById(R.id.container);

        AnimationDrawable anim = (AnimationDrawable) container.getBackground();
        anim.setEnterFadeDuration(6000);
        anim.setExitFadeDuration(2000);

        try {
            JSONObject response = new JSONObject((String) Objects.requireNonNull(b.get("response")));
            title.setText(response.getString("intentName"));
            text.setText(response.getString("text"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
