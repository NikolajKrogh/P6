package com.example.p6;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

import com.example.p6.databinding.ActivityMainBinding;

import java.util.List;

public class MainActivity extends Activity implements SensorEventListener {

    SensorManager mSensorManager;
    Sensor accSensor;
    Sensor stepSensor;
    String x_axis;

    private TextView statusLabel;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        stepSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        //mSensorManager.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        statusLabel = binding.statusLabel;
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        x_axis = Float.toString(event.values[0]);
        statusLabel.setText(x_axis);
        Log.d("Sensor:", event.sensor.getStringType());
    }

    public void onButtonClick(View view) {
        statusLabel.setText(x_axis);
    }
}