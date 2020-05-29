package com.softdev.kuleuven.thesis.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.softdev.kuleuven.thesis.MainActivity;
import com.softdev.kuleuven.thesis.R;

import java.io.IOException;

public class NewsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        MediaPlayer mediaPlayer = new MediaPlayer();

        ImageView close = findViewById(R.id.imageView7);

        try {
            String previewURL = "http://progressive-audio.lwc.vrtcdn.be/content/fixed/11_11niws-snip_hi.mp3";
            mediaPlayer.setDataSource(previewURL);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
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
