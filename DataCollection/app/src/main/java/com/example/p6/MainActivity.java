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
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public abstract class MainActivity extends Activity implements SensorEventListener {

    SensorManager mSensorManager;
    private Sensor senAccelerometer;
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
            accelerometerText.setText("accelerometer:" +x_axis + "x: , " + y_axis + "y: , " + z_axis + "z: ");
            //Log.i("x,y,z", x_axis + ", " + y_axis + ", " + z_axis);
        }

        Log.i("Hello world!", "Hello world!");
        File file = new File("\\P6\\Python");
        try {
            Log.i("Hello from the other side", "Hello from the other side");
            // create FileWriter object with file as parameter
            FileWriter outputFile = new FileWriter(file);

            // create CSVWriter object fileWriter object as parameter
            CSVWriter writer = new CSVWriter(outputFile);

            // adding header to csv
            String[] header = { "Name", "Class", "Marks" };
            writer.writeNext(header);

            // add data to csv
            String[] data1 = { "Aman", "10", "620" };
            writer.writeNext(data1);
            String[] data2 = { "Suraj", "10", "630" };
            writer.writeNext(data2);

            // closing writer connection
            writer.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void onButtonClick(View view) {
        Log.i("Sensors", mSensorManager.getSensorList(Sensor.TYPE_ALL).toString());
    }
}



