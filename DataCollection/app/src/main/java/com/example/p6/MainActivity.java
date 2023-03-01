package com.example.p6;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.example.p6.databinding.ActivityMainBinding;

public class MainActivity extends Activity implements SensorEventListener {

    SensorManager mSensorManager;
    Sensor senAccelerometer;
    private Sensor senHeartRateCounter;
    private String heartRate;
    String x_axis;
    String y_axis;
    String z_axis;

    private TextView accelerometerText;
    private TextView heartRateText;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        senAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senHeartRateCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);


        mSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, senHeartRateCounter, SensorManager.SENSOR_DELAY_FASTEST);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        accelerometerText = binding.accelerometerText;
        heartRateText = binding.heartRateText;
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType()==Sensor.TYPE_HEART_RATE){
            heartRate = Float.toString(event.values[0]);
            Log.i("Heart Rate:", heartRate);
            heartRateText.setText("Heart Rate:" + heartRate);
        }
        if (event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            x_axis = Float.toString(event.values[0]);
            y_axis = Float.toString(event.values[1]);
            z_axis = Float.toString(event.values[2]);
            accelerometerText.setText("accelerometer: x: " +x_axis + ", y: " + y_axis + ", z: " + z_axis);
            //Log.i("x,y,z", x_axis + ", " + y_axis + ", " + z_axis);
        }
    }

    public void onButtonClick(View view) {
        Log.i("Sensors", mSensorManager.getSensorList(Sensor.TYPE_ALL).toString());
    }
}

