package com.softdev.kuleuven.thesis.helpers;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.text.format.Time;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

import static android.content.ContentValues.TAG;


@SuppressWarnings({"ALL", "ResultOfMethodCallIgnored"})
public class RecordingHelper {
    private static final int[] samplingRates = {16000, 11025, 11000, 8000, 6000};
    private static int SAMPLE_RATE = 16000;
    private AudioRecord mRecorder;
    private File mRecording;
    private short[] mBuffer;
    private String audioFilePath;
    private boolean mIsRecording = false;
    private final String RECORD_WAV_PATH = Environment.getExternalStorageDirectory() + File.separator + "AudioRecord";
    private static RecordingHelper _instance;

    /* Initializing AudioRecording MIC */
    private RecordingHelper() {
        initRecorder();
    }

    public static RecordingHelper getInstance() {
        if(_instance == null) {
            _instance = new RecordingHelper();
        }
        return _instance;
    }

    /* Get Supported Sample Rate */
    private static int getValidSampleRates() {
        for (int rate : samplingRates) {
            int bufferSize = AudioRecord.getMinBufferSize(rate, AudioFormat.CHANNEL_CONFIGURATION_DEFAULT, AudioFormat.ENCODING_PCM_16BIT);
            if (bufferSize > 0) {
                return rate;
            }
        }
        return SAMPLE_RATE;
    }

    /* Start AudioRecording */
    public void recordWavStart() {
        Log.i(TAG, "recordWavStart: Recording started");
        mIsRecording = true;
        mRecorder.startRecording();
        mRecording = getFile("raw");
        startBufferedWrite(mRecording);
    }

    /* Stop AudioRecording */
    public File recordWavStop() {
        try {
            mIsRecording = false;
            mRecorder.stop();
            File waveFile = getFile("wav");
            rawToWave(mRecording, waveFile);
            Log.e("path_audioFilePath",audioFilePath);
            //byte[] soundOutput = readFileToByteArray(waveFile);
            return waveFile;
        } catch (Exception e) {
            Log.e("Error saving file : ", Objects.requireNonNull(e.getMessage()));
        }
        return  null;
    }

    /* Release device MIC */
    public void releaseRecord() {
        mRecorder.release();
    }

    /* Initializing AudioRecording MIC */
    private void initRecorder() {
        SAMPLE_RATE = getValidSampleRates();
        int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        mBuffer = new short[bufferSize];
        mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT, bufferSize);
        new File(RECORD_WAV_PATH).mkdir();
    }

    /* Writing RAW file */
    private void startBufferedWrite(final File file) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DataOutputStream output = null;
                try {
                    output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
                    while (mIsRecording) {
                        double sum = 0;
                        int readSize = mRecorder.read(mBuffer, 0, mBuffer.length);
                        for (int i = 0; i < readSize; i++) {
                            output.writeShort(mBuffer[i]);
                            sum += mBuffer[i] * mBuffer[i];
                        }
                        if (readSize > 0) {
                            final double amplitude = sum / readSize;
                        }
                    }
                } catch (IOException e) {
                    Log.e("Error writing file : ", Objects.requireNonNull(e.getMessage()));
                } finally {

                    if (output != null) {
                        try {
                            output.flush();
                        } catch (IOException e) {
                            Log.e("Error writing file : ", Objects.requireNonNull(e.getMessage()));
                        } finally {
                            try {
                                output.close();
                            } catch (IOException e) {
                                Log.e("Error writing file : ", Objects.requireNonNull(e.getMessage()));
                            }
                        }
                    }
                }
            }
        }).start();
    }

    /* Converting RAW format To WAV Format*/
    private void rawToWave(final File rawFile, final File waveFile) throws IOException {

        byte[] rawData = new byte[(int) rawFile.length()];
        try (DataInputStream input = new DataInputStream(new FileInputStream(rawFile))) {
            input.read(rawData);
        }
        DataOutputStream output = null;
        try {
            output = new DataOutputStream(new FileOutputStream(waveFile));
            // WAVE header
            writeString(output, "RIFF"); // chunk id
            writeInt(output, 36 + rawData.length); // chunk size
            writeString(output, "WAVE"); // format
            writeString(output, "fmt "); // subchunk 1 id
            writeInt(output, 16); // subchunk 1 size
            writeShort(output, (short) 1); // audio format (1 = PCM)
            writeShort(output, (short) 1); // number of channels
            writeInt(output, SAMPLE_RATE); // sample rate
            writeInt(output, SAMPLE_RATE * 2); // byte rate
            writeShort(output, (short) 2); // block align
            writeShort(output, (short) 16); // bits per sample
            writeString(output, "data"); // subchunk 2 id
            writeInt(output, rawData.length); // subchunk 2 size
            // Audio data (conversion big endian -> little endian)
            short[] shorts = new short[rawData.length / 2];
            ByteBuffer.wrap(rawData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
            ByteBuffer bytes = ByteBuffer.allocate(shorts.length * 2);
            for (short s : shorts) {
                bytes.putShort(s);
            }
            output.write(bytes.array());
        } finally {
            if (output != null) {
                output.close();
                rawFile.delete();
            }
        }


    }

    /* Get file name */
    private File getFile(final String suffix) {
        Time time = new Time();
        time.setToNow();
        audioFilePath = time.format("%Y%m%d%H%M%S");
        return new File(RECORD_WAV_PATH, time.format("%Y%m%d%H%M%S") + "." + suffix);
    }

    private void writeInt(final DataOutputStream output, final int value) throws IOException {
        output.write(value);
        output.write(value >> 8);
        output.write(value >> 16);
        output.write(value >> 24);
    }

    private void writeShort(final DataOutputStream output, final short value) throws IOException {
        output.write(value);
        output.write(value >> 8);
    }

    private void writeString(final DataOutputStream output, final String value) throws IOException {
        for (int i = 0; i < value.length(); i++) {
            output.write(value.charAt(i));
        }
    }

    public String getFileName (final String time_suffix) {
        return (RECORD_WAV_PATH+time_suffix+ "." + "wav");
    }

    public static String encode(File inputFile) throws IOException {
        byte[] data = new byte[(int) inputFile.length()];
        DataInputStream input = new DataInputStream(new FileInputStream(inputFile));
        int readBytes = input.read(data);
        Log.i(TAG, readBytes + " read from input file.");
        input.close();
        return Base64.encodeToString(data, Base64.NO_WRAP);
    }

    public Boolean getRecordingState () {
        return mRecorder.getRecordingState() != AudioRecord.RECORDSTATE_STOPPED;
    }
}
