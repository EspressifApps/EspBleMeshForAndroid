package com.espressif.espblemesh.utils;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;

public class Wisper {
    private static final String TAG = "Wisper";

    private static Wisper instance;

    private Wisper() {
    }

    public static Wisper getInstance() {
        if (instance == null) {
            synchronized (Wisper.class) {
                if (instance == null) {
                    instance = new Wisper();
                }
            }
        }

        return instance;
    }

    private static final int SAMPLE_RATE_IN_HZ = 8000;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_DEFAULT;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE_IN_HZ,
            CHANNEL_CONFIG, AUDIO_FORMAT);

    private final ReentrantLock mLock = new ReentrantLock();
    private AudioRecord mAudioRecord;
    private volatile boolean mRecording = false;

    public void startListenVolume(OnVolumeChangedListener listener) {
        stop();

        mLock.lock();
        AudioRecord record;
        try {
            record = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE_IN_HZ,
                    CHANNEL_CONFIG, AUDIO_FORMAT, BUFFER_SIZE);
            mAudioRecord = record;
        } catch (Exception e) {
            e.printStackTrace();
            return;
        } finally {
            mLock.unlock();
        }

        record.startRecording();
        mRecording = true;
        short[] buffer = new short[BUFFER_SIZE];
        while (mRecording) {
            int read = record.read(buffer, 0, BUFFER_SIZE);
            if (read <= 0) {
                Log.w(TAG, "start: read: " + read);
                break;
            }
            short[] data = Arrays.copyOf(buffer, read);
            double volume = calculateVolume(data);
            if (listener != null) {
                listener.onVolumeChanged(volume);
            }
        }

        record.stop();
        mRecording = false;
        Log.d(TAG, "startListenVolume: end");
    }

    public void stop() {
        mLock.lock();
        mRecording = false;
        if (mAudioRecord != null) {
            mAudioRecord.stop();
            mAudioRecord = null;
        }
        mLock.unlock();
    }

    private static double calculateVolume(short[] buffer) {
        double volume;
        double sumVolume = 0.0;
        for (short b : buffer) {
            sumVolume += Math.abs(b);

        }
        double avgVolume = sumVolume / buffer.length;
        volume = Math.log10(1 + avgVolume) * 10;

        return volume;
    }

    public interface OnVolumeChangedListener {
        void onVolumeChanged(double volume);
    }
}
