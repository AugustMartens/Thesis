package com.softdev.kuleuven.thesis.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.softdev.kuleuven.thesis.EndActivity;
import com.softdev.kuleuven.thesis.MainActivity;
import com.softdev.kuleuven.thesis.R;

/**
 * Skeleton of an Android Things activity.
 */
public class FailedActivity extends EndActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_failed);

        ImageView close = findViewById(R.id.imageView4);

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
