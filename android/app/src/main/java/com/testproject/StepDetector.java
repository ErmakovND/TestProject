package com.testproject;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

public class StepDetector extends ReactContextBaseJavaModule implements SensorEventListener {

    private ReactApplicationContext mReactContext;
    private SensorManager mSensorManager;
    private Sensor mStepDetector;
    private boolean isActive = false;

    public StepDetector(ReactApplicationContext reactApplicationContext) {
        super(reactApplicationContext);
        mReactContext = reactApplicationContext;
        mSensorManager = (SensorManager) mReactContext.getSystemService(Context.SENSOR_SERVICE);
        mStepDetector = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
    }

    private boolean isAvailable() {
        return mStepDetector != null;
    }

    private void sendEvent() {
        WritableMap params = Arguments.createMap();
        params.putString("Step", "detected");
        mReactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit("StepDetector", params);
        Log.d("StepDetector", "event sent");
    }

    @ReactMethod
    public void isAvailable(Promise promise) {
        promise.resolve(isAvailable());
    }

    @ReactMethod
    public void subscribe(Promise promise) {
        if (!isAvailable()) {
            promise.reject("Sensor error", "StepCounter is not available");
        }
        mSensorManager.registerListener(this, mStepDetector, SensorManager.SENSOR_DELAY_NORMAL);
        isActive = true;
        promise.resolve(isActive);
    }

    @ReactMethod
    public void unsubscribe(Promise promise) {
        if (!isAvailable()) {
            promise.reject("Sensor error", "StepCounter is not available");
        }
        if (isActive) {
            mSensorManager.unregisterListener(this, mStepDetector);
            isActive = false;
        }
        promise.resolve(isActive);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d("StepDetector", "Step detected");
        sendEvent();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @NonNull
    @Override
    public String getName() {
        return "StepDetector";
    }
}
