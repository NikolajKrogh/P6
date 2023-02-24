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
    String x_axis;
    String y_axis;
    String z_axis;

    private TextView statusLabel;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //mSensorManager.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_NORMAL);

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
        y_axis = Float.toString(event.values[1]);
        z_axis = Float.toString(event.values[2]);
        statusLabel.setText(x_axis + ", " + y_axis + ", " + z_axis);
        //Log.i("x,y,z", x_axis + ", " + y_axis + ", " + z_axis);
    }

    public void onButtonClick(View view) {
        Log.i("Sensors", mSensorManager.getSensorList(Sensor.TYPE_ALL).toString());
    }
}