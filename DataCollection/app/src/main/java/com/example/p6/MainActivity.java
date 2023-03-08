package com.example.p6;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Toast;

import com.example.p6.databinding.ActivityMainBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        String step_count_rate;
        String label;
        String accuracy;

        public Row(String timestamp, String heartbeat, String acc_x, String acc_y, String acc_z, String step_count_rate, String label, String accuracy)
        {
            this.timestamp = timestamp;
            this.heartbeat = heartbeat;
            this.acc_x = acc_x;
            this.acc_y = acc_y;
            this.acc_z = acc_z;
            this.step_count_rate = step_count_rate;
            this.label = label;
            this.accuracy = accuracy;
        }
        @Override
        public String toString()
        {
            return String.format("%s,%s,%s,%s,%s,%s,%s,%s\n", timestamp,heartbeat,acc_x,acc_y,acc_z,step_count_rate,label,accuracy);
        }
    }
    //String[][] rows = {};
    private List<Row> rows = new ArrayList();
    private SensorManager mSensorManager;
    private Sensor senAccelerometer;
    private Sensor senStepDetector;
    private Sensor senStepCounter;
    private Sensor senHeartRateCounter;
    float heartRate = 0;
    long latestTimestamp = 0;
    long currentTimestamp = 0;
    float firstStepCount = 0;
    float lastStepCount = 0;
    float currentStepCount = 0;
    float stepCountRate = 0;
    short stepCountIteration = 0;
    private static final long MILLISEC_TO_NANOSEC_FACTOR = 1000000;
    private TextView accelerometerText;
    private TextView heartRateText;
    private ActivityMainBinding binding;
    short heartRateAccuracy = 0;
    enum Activity {
        IDLE,
        WALKING,
        RUNNING,
        CYCLING;
    }

    Activity activityToTrack = Activity.WALKING;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        senAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senHeartRateCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        senStepCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        accelerometerText = binding.accelerometerText;
        heartRateText = binding.heartRateText;
    }

    public void insertDataAtTimeStamp(long timestamp, float heartRate, float acc_x, float acc_y, float acc_z, float step_count_rate, short accuracy, List<Row> rows)
    {
        int label = activityToTrack.ordinal();
        Row row = new Row(
                Long.toString(timestamp),
                Float.toString(heartRate),
                Float.toString(acc_x),
                Float.toString(acc_y),
                Float.toString(acc_z),
                Float.toString(step_count_rate),
                Integer.toString(label),
                Short.toString(accuracy)
        );
        rows.add(row);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        long time = event.timestamp;
        if(event.sensor.getType() == Sensor.TYPE_STEP_COUNTER){
            currentStepCount = event.values[0];
        }
        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE && event.values[0] > 0) {
            heartRate = event.values[0];
            heartRateText.setText("Heart Rate: " + heartRate);
        }
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && heartRate > 0 && heartRateAccuracy != 0) {
            currentTimestamp = event.timestamp;
            if (currentTimestamp - latestTimestamp > 100 * MILLISEC_TO_NANOSEC_FACTOR) {
                stepCountIteration++;
                if (stepCountIteration == 1){
                    firstStepCount = currentStepCount;
                }
                else if (stepCountIteration >= 50){
                    lastStepCount = currentStepCount;
                    stepCountRate = lastStepCount - firstStepCount;
                    Log.i("Rate: ", Float.toString(stepCountRate));
                    stepCountIteration = 0;
                }
                float x_axis = event.values[0];
                float y_axis = event.values[1];
                float z_axis = event.values[2];
                insertDataAtTimeStamp(time, heartRate, x_axis, y_axis, z_axis, stepCountRate, heartRateAccuracy, rows);
                accelerometerText.setText("Accelerometer: x: " + x_axis + ", y: " + y_axis + ", z: " + z_axis);
                latestTimestamp = currentTimestamp;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracyReceived) {
        if (sensor == senHeartRateCounter) {
            heartRateAccuracy = (short) accuracyReceived;
        }
    }

    public void onStartButtonClick(View view){
        view.setEnabled(false);
        findViewById(R.id.exitButton).setEnabled(false);
        findViewById(R.id.stopButton).setEnabled(true);
        RadioGroup radioButtons = (RadioGroup)findViewById(R.id.radioButtonGroup);
        for(int i = 0; i < radioButtons.getChildCount(); i++){
            radioButtons.getChildAt(i).setClickable(false);
        }
        Toast.makeText(getApplicationContext(), "Tracking started for " + activityToTrack.name().toLowerCase(), Toast.LENGTH_SHORT).show();
        mSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, senHeartRateCounter, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, senStepCounter, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onStopButtonClick(View view) {
        mSensorManager.unregisterListener(this, senAccelerometer);
        mSensorManager.unregisterListener(this, senHeartRateCounter);
        findViewById(R.id.stopButton).setEnabled(false);
        String finalString = "timestamp,heart_rate,acc_x,acc_y,acc_z,step_count_rate,label,heart_rate_accuracy\n";

        for (Row row: rows)
        {
            finalString += row.toString();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.now();
        Toast.makeText(getApplicationContext(), "Writing to file ...", Toast.LENGTH_SHORT).show();
        writeToFile(activityToTrack.name().toLowerCase() + "_" + formatter.format(dateTime) + ".csv", finalString);

        findViewById(R.id.startButton).setEnabled(true);
        findViewById(R.id.exitButton).setEnabled(true);
        rows.clear();

        RadioGroup radioButtons = (RadioGroup)findViewById(R.id.radioButtonGroup);
        for(int i = 0; i < radioButtons.getChildCount(); i++){
            radioButtons.getChildAt(i).setClickable(true);
        }
    }

    public void onExitButtonClick(View view) {
        finish();
        System.exit(0);
    }

    public void writeToFile(String fileName, String content){

        File path = null;
        try {
            path = getApplicationContext().getDir(fileName, Context.MODE_PRIVATE); // Use MODE_APPEND if you don't want to overwrite the content
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

    public void onRadioButtonIdle(View view) {
        activityToTrack = Activity.IDLE;
    }
    public void onRadioButtonWalking(View view) {
        activityToTrack = Activity.WALKING;
    }
    public void onRadioButtonRunning(View view) {
        activityToTrack = Activity.RUNNING;
    }
    public void onRadioButtonCycling(View view) {
        activityToTrack = Activity.CYCLING;
    }
}

