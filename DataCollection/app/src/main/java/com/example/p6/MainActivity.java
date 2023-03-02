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
        long timestamp;
        String heartbeat;
        String acc_x;
        String acc_y;
        String acc_z;
        String label;

        public Row(long timestamp, String label)
        {
            this.timestamp = timestamp;
            this.label = label;
        }
        public String[] toList()
        {
            return new String[] {Long.toString(timestamp),heartbeat,acc_x,acc_y,acc_z,label};
        }

        public String toString()
        {
            return String.format("%s,%s,%s,%s,%s,%s\n", Long.toString(timestamp),heartbeat,acc_x,acc_y,acc_z,label);
        }


    }
    //String[][] rows = {};
    List<Row> rows = new ArrayList();
    SensorManager mSensorManager;
    Sensor senAccelerometer;
    private Sensor senHeartRateCounter;
    private String heartRate;
    String x_axis;
    String y_axis;
    String z_axis;
    long firstTime = 0;
    int i = -1000;
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

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {

    }


    public void insertAccelerometerDataAtTimestamp(float[] accelerometerEventValues, long timestamp , List<Row> rows)
    {
        for (Row row: rows) {
            if (row.timestamp == timestamp)
            {
                row.acc_x = Float.toString(accelerometerEventValues[0]);
                row.acc_y = Float.toString(accelerometerEventValues[1]);
                row.acc_z = Float.toString(accelerometerEventValues[2]);
            }
        }
    }

    private void insertHeartbeatDataAtTimestamp(float heartbeat, long timestamp , List<Row> rows)
    {
        for (Row row: rows) {
            if (row.timestamp == timestamp)
            {
                row.heartbeat = Float.toString(heartbeat);
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (i >= 100 || event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
            if (firstTime == 0)
                firstTime = event.timestamp;
            long time = event.timestamp;
            int label = 0; //0 for walking, 1 for running
            rows.add(new Row(time, Integer.toString(label)));
            if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
                insertHeartbeatDataAtTimestamp(event.values[0], time, rows);
                heartRate = Float.toString(event.values[0]);
                Log.i("Heart Rate:", heartRate);
                heartRateText.setText("Heart Rate:" + heartRate);
                hasGotHeartBeatData = true;
            }
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                insertAccelerometerDataAtTimestamp(event.values, time, rows);
                x_axis = Float.toString(event.values[0]);
                y_axis = Float.toString(event.values[1]);
                z_axis = Float.toString(event.values[2]);
                accelerometerText.setText("accelerometer: x: " + x_axis + ", y: " + y_axis + ", z: " + z_axis);
                //Log.i("x,y,z", x_axis + ", " + y_axis + ", " + z_axis);
            }
            if (hasGotHeartBeatData){
                i = 100;
                hasGotHeartBeatData = false;
            }
            else {
                i = 0;
            }
        }
        else {
            i++;
        }
    }

    public void onButtonClick(View view) {
        //String[] header = { "timestamp,heartbeat,acc_x,acc_y,acc_z,label"};
        String finalString = "timestamp,heartbeat,acc_x,acc_y,acc_z,label\n";

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

