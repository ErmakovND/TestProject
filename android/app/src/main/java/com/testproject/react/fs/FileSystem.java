package com.testproject.react.fs;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

public class FileSystem extends ReactContextBaseJavaModule {

    private ReactApplicationContext mReactContext;

    public FileSystem(@Nullable ReactApplicationContext reactContext) {
        super(reactContext);
        mReactContext = reactContext;
    }

    @ReactMethod
    public void getAudioFiles(Promise promise) {
        Uri collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[] {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DATA,
        };
        try (Cursor cursor = mReactContext.getContentResolver().query(
                collection,
                projection,
                null,
                null,
                null
        )) {
            WritableArray files = Arguments.createArray();
            while (cursor.moveToNext()) {
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                WritableMap map = Arguments.createMap();
                map.putString("data", data);
                files.pushMap(map);
            }
            promise.resolve(files);
        } catch (Exception e) {
            promise.reject("Exception", e.getMessage());
        }
    }

    @NonNull
    @Override
    public String getName() {
        return "FileSystem";
    }
}
