package com.example.p6;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;

import com.example.p6.databinding.ActivityMainBinding;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements SensorEventListener {
    class Row
    {
        String timestamp;
        String heartbeat;
        String acc_x;
        String acc_y;
        String acc_z;
        String label;

        public Row(String timestamp, String heartbeat, String acc_x, String acc_y, String acc_z, String label)
        {
            this.timestamp = timestamp;
            this.heartbeat = heartbeat;
            this.label = label;
            this.acc_x = acc_x;
            this.acc_y = acc_y;
            this.acc_z = acc_z;
        }

        public String toString()
        {
            return String.format("%s,%s,%s,%s,%s,%s\n", timestamp,heartbeat,acc_x,acc_y,acc_z,label);
        }


    }
    //String[][] rows = {};
    List<Row> rows = new ArrayList();
    SensorManager mSensorManager;
    Sensor senAccelerometer;
    private Sensor senHeartRateCounter;
    float heartRate = 0;
    boolean hasGotHeartBeatData = false;

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

    public void insertDataAtTimeStamp(long timestamp, float heartRate, float acc_x, float acc_y, float acc_z , List<Row> rows)
    {
        int label = 0; //0 for walking, 1 for running
        Row row = new Row(
                Long.toString(timestamp),
                Float.toString(heartRate),
                Float.toString(acc_x),
                Float.toString(acc_y),
                Float.toString(acc_z),
                Integer.toString(label)
        );

        rows.add(row);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        long time = event.timestamp;
        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
            heartRate = event.values[0];
            heartRateText.setText("Heart Rate:" + heartRate);
            hasGotHeartBeatData = true;
        }
        if (hasGotHeartBeatData && event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x_axis = event.values[0];
            float y_axis = event.values[1];
            float z_axis = event.values[2];
            insertDataAtTimeStamp(time, heartRate, x_axis, y_axis, z_axis, rows);
            accelerometerText.setText("accelerometer: x: " + x_axis + ", y: " + y_axis + ", z: " + z_axis);
            hasGotHeartBeatData = false;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void onButtonClick(View view) {
        //String[] header = { "timestamp,heartbeat,acc_x,acc_y,acc_z,label"};
        String finalString = "timestamp,heartrate,acc_x,acc_y,acc_z,label\n";

        for (Row row: rows)
        {
            finalString += row.toString();
        }
        writeToFile("collectedData.csv", finalString);
    }

    public void writeToFile(String fileName, String content){
        File path = null;
        try {
            path = getApplicationContext().getDir(fileName, Context.MODE_APPEND); // Use MODE_APPEND if you don't want to overwrite the content
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            FileOutputStream writer = new FileOutputStream(new File(path, fileName));
            writer.write(content.getBytes());
            writer.close();
            Toast.makeText(getApplicationContext(), "Wrote to file: " + fileName, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

