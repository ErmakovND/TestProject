package com.testproject.react.audio;

import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.testproject.service.audio.AudioUtil;
import com.testproject.service.audio.JLib;

import java.io.IOException;

public class Audio extends ReactContextBaseJavaModule {

    @ReactMethod
    public void getBPM(String path, Promise promise) {
        try {
            byte[] bytes = AudioUtil.load(path);
            Log.i("Audio", "Loaded " + bytes.length + " bytes");
            float[] floats = JLib.toFloat(bytes);
            Log.i("Audio", "Loaded " + floats.length + " floats");
            double bpm = JLib.getBPM(floats);
            promise.resolve(bpm);
        } catch (IOException | InterruptedException e) {
            promise.reject(e);
        }
    }

    @NonNull
    @Override
    public String getName() {
        return "Audio";
    }
}
