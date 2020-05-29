package com.softdev.kuleuven.thesis.activities;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.palette.graphics.Palette;

import com.softdev.kuleuven.thesis.MainActivity;
import com.softdev.kuleuven.thesis.R;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;


/**
 *
 */

public class MusicActivity extends Activity {

    private String album;
    private String title;
    private String artist;
    private String albumArt;
    private String previewURL;

    private TextView titleView;
    private TextView albumView;
    private TextView artistView;

    private ConstraintLayout currentLayout;

    private MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        Intent iin= getIntent();
        Bundle b = iin.getExtras();
        albumView = findViewById(R.id.albumView);
        titleView = findViewById(R.id.TitleView);
        artistView = findViewById(R.id.ArtistView);
        currentLayout = findViewById(R.id.activity_music);
        ImageView albumArtView = findViewById(R.id.albumArtView);
        ImageView close = findViewById(R.id.imageView2);


        try {
            JSONObject response = new JSONObject((String) Objects.requireNonNull(Objects.requireNonNull(b).get("response")));
            title = response.getJSONObject("parameters").getJSONObject("nummerNaam").getString("stringValue");
            album = response.getJSONObject("parameters").getJSONObject("album").getString("stringValue");
            artist = response.getJSONObject("parameters").getJSONObject("artiest").getString("stringValue");
            albumArt = response.getJSONObject("parameters").getJSONObject("imgUrl").getString("stringValue");
            previewURL = response.getJSONObject("parameters").getJSONObject("prevUrl").getString("stringValue");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("Title: ",title);
        titleView.setText(title);
        Log.d("Artiest: ",artist);
        artistView.setText(artist);
        Log.d("Album: ",album);
        albumView.setText(album);
        Log.d("imgUrl: ",albumArt);
        new DownloadImageTask(albumArtView)
                .execute(albumArt);
        Log.d("prevUrl: ",previewURL);


        mediaPlayer = new MediaPlayer();
        try {
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

    private void updateBgColor(Bitmap bitmap) {
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette palette) {
                Palette.Swatch swatch = palette.getVibrantSwatch();
                if (swatch == null) swatch = palette.getMutedSwatch(); // Sometimes vibrant swatch is not available
                if (swatch != null) {
                    //Set an id to the layout
                    // Set the background color of the player bar based on the swatch color
                    currentLayout.setBackgroundColor(swatch.getRgb());

                    // Update the track's title with the proper title text color
                    titleView.setTextColor(swatch.getTitleTextColor());

                    // Update the artist name with the proper body text color
                    albumView.setTextColor(swatch.getBodyTextColor());
                    artistView.setTextColor(swatch.getBodyTextColor());
                }
            }
        });
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        final ImageView bmImage;
        private String TAG = "DownloadImageTask";

        DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", Objects.requireNonNull(e.getMessage()));
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
            updateBgColor(result);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
    }
}
