package com.softdev.kuleuven.thesis;


import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Main Activity voor Smartspeaker
 * August Martens 2020
 * <p>
 * On first boot please load the wakeword files in memory by commenting out lowest part.
 */

public class MainActivity extends BaseActivity {

    private TextView timeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        try {
//            copyPorcupineResourceFiles();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View view = findViewById(R.id.layout);
        timeView = findViewById(R.id.textView);
        CardView cardView = findViewById(R.id.card);
        TextView query = findViewById(R.id.text);

        cardView.setVisibility(View.GONE);

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                goToSpeech();
                return false;
            }
        });
        refreshTime();
    }

    //this method is used to refresh Time every Second
    private void refreshTime() //Call this method to refresh time
    {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Date time = Calendar.getInstance().getTime();
                        SimpleDateFormat outputFmt = new SimpleDateFormat("HH:mm");
                        outputFmt.setTimeZone(TimeZone.getTimeZone("Europe/Brussels"));
                        timeView.setText(outputFmt.format(time));
                    }
                });
            }
        }, 0, 5000);//1000 is a Refreshing Time (1second)
    }


    //    private void copyResourceFile(int resourceID, String filename) throws IOException {
//        Resources resources = getResources();
//        try (InputStream is = new BufferedInputStream(resources.openRawResource(resourceID), 256); OutputStream os = new BufferedOutputStream(openFileOutput(filename, Context.MODE_PRIVATE), 256)) {
//            int r;
//            while ((r = is.read()) != -1) {
//                os.write(r);
//            }
//            os.flush();
//        }
//    }
//
//    private static int[] KEYWORD_FILE_RESOURCE_IDS = {
//            R.raw.americano, R.raw.blueberry, R.raw.bumblebee, R.raw.grapefruit,
//            R.raw.grasshopper, R.raw.picovoice, R.raw.porcupine, R.raw.terminator,
//    };
//
//    private void copyPorcupineResourceFiles() throws IOException {
//        Resources resources = getResources();
//
//        for (int keywordFileResourceID : KEYWORD_FILE_RESOURCE_IDS) {
//            copyResourceFile(keywordFileResourceID, resources.getResourceEntryName(keywordFileResourceID) + ".ppn");
//        }
//
//        copyResourceFile(R.raw.porcupine_params, resources.getResourceEntryName(R.raw.porcupine_params) + ".pv");
//    }

}

