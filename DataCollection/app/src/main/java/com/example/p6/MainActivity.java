package com.example.p6;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.p6.databinding.ActivityMainBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements SensorEventListener {
    static class Row{
        String timestamp;
        String heartbeat;
        String acc_x;
        String acc_y;
        String acc_z;
        String step_count_rate;
        String step_count;
        String label;
        String accuracy;

        public Row(String timestamp, String heartbeat, String acc_x, String acc_y, String acc_z,
                   String step_count_rate, String step_count, String label, String accuracy)
        {
            this.timestamp = timestamp;
            this.heartbeat = heartbeat;
            this.acc_x = acc_x;
            this.acc_y = acc_y;
            this.acc_z = acc_z;
            this.step_count_rate = step_count_rate;
            this.step_count = step_count;
            this.label = label;
            this.accuracy = accuracy;
        }
        @NonNull
        @Override
        public String toString(){
            return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s\n", timestamp,heartbeat,
                    acc_x,acc_y,acc_z,step_count_rate,step_count,label,accuracy);
        }
    }

    //region Enums
    enum Activity {
        IDLE,
        WALKING,
        RUNNING,
        CYCLING
    }
    //endregion

    //region Time constants
    private static final long MILLISEC_TO_NANOSEC_FACTOR = 1000000;
    private static final long SEC_TO_MILLISEC_FACTOR =  1000;
    private static final long MIN_TO_SEC_FACTOR =  60;
    private static final long HOUR_TO_MIN_FACTOR =  60;
    //endregion

    //region Accelerometer variables
    private Sensor senAccelerometer;
    private TextView accelerometerText;
    //endregion

    //region Heart rate variables
    private Sensor senHeartRateCounter;
    private TextView heartRateText;
    private float heartRate = 0;
    private short heartRateAccuracy = 0;
    private long firstTimestamp = 0;
    private long latestTimestamp = 0;
    //endregion

    //region Step count variables
    private Sensor senStepCounter;
    private TextView stepCountText;
    private TextView stepCountRateText;
    private float initialStepCount = -1; // Initialized as -1 because the initial step count will
                                         // never be -1, but it may be 0
    private float accumulatedStepCount = 0;
    private float firstStepCount = 0;
    private float lastStepCount = 0;
    private float currentStepCount = 0;
    private float stepCountRate = 0;
    private short stepCountCounter = 0;
    //endregion

    //region Data point variables
    private List<Row> rows = new ArrayList();
    private short numberOfDataPointsAdded = 0;
    private String dataPointsToAdd;
    //endregion

    //region Formatters
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");
    private final DecimalFormat decimalFormat = new DecimalFormat("#0.00");
    private final DecimalFormat clockFormat = new DecimalFormat("#00");

    //endregion

    //region Other global variables
    private SensorManager mSensorManager;
    private Activity activityToTrack = Activity.WALKING;
    private LocalDateTime dateTime;
    private TextView timerText;
    private TextView timesWrittenToFileText;
    private int timesWrittenToFile = 0;

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSensors();
        bindTextToVariables();
    }

    public void getSensors(){
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        senAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senHeartRateCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        senStepCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
    }

    public void bindTextToVariables(){
        com.example.p6.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        accelerometerText = binding.accelerometerText;
        heartRateText = binding.heartRateText;
        stepCountText = binding.stepCountText;
        stepCountRateText = binding.stepCountRateText;
        timerText = binding.timerText;
        timesWrittenToFileText = binding.timesWrittenToFileText;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_STEP_COUNTER){
            updateAccumulatedStepCount(event);
        }
        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE && event.values[0] > 0) {
            heartRate = event.values[0];
            heartRateText.setText("Heart Rate: " + heartRate);
        }
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && heartRate > 0 && heartRateAccuracy != 0) {
            long currentTimestamp = event.timestamp;
            updateTimeSinceStart(currentTimestamp);
            if (currentTimestamp - latestTimestamp > 100 * MILLISEC_TO_NANOSEC_FACTOR) {
                updateStepCountRate();
                float x_axis = event.values[0];
                float y_axis = event.values[1];
                float z_axis = event.values[2];
                if (numberOfDataPointsAdded <= 500){
                    insertDataAtTimeStamp(currentTimestamp, heartRate, x_axis, y_axis, z_axis, stepCountRate,  accumulatedStepCount, heartRateAccuracy, rows);
                    numberOfDataPointsAdded++;
                }
                else {
                    writeToFile(activityToTrack.name().toLowerCase() + "_" + dateTimeFormatter.format(dateTime) + ".csv", dataPointsToAdd);
                    timesWrittenToFile++;
                    timesWrittenToFileText.setText("Written to file " + timesWrittenToFile + " times");
                    numberOfDataPointsAdded = 0;
                }
                ProgressBar dataPointProgressBar = findViewById(R.id.dataPointProgressBar); // initiate the progress bar
                dataPointProgressBar.setProgress(numberOfDataPointsAdded);
                accelerometerText.setText("x: " + decimalFormat.format(x_axis)
                        + "   y: " + decimalFormat.format(y_axis)
                        + "   z: " + decimalFormat.format(z_axis));
                latestTimestamp = currentTimestamp;
            }
        }
    }

    public void updateTimeSinceStart(long currentTimestamp){
        if (firstTimestamp == 0){
            firstTimestamp = currentTimestamp;
            Log.i("First", ": " + firstTimestamp);
            Log.i("First current", ": " + currentTimestamp);
        }

        long nanosecondsSinceStart = currentTimestamp - firstTimestamp;
        long milliSecondsSinceStart = nanosecondsSinceStart / MILLISEC_TO_NANOSEC_FACTOR;
        long secondsSinceStart = milliSecondsSinceStart / SEC_TO_MILLISEC_FACTOR;
        long minutesSinceStart = secondsSinceStart / MIN_TO_SEC_FACTOR;

        long hoursToDisplay = minutesSinceStart / HOUR_TO_MIN_FACTOR;
        long minutesToDisplay = minutesSinceStart % 60;
        long secondsToDisplay = secondsSinceStart % 60;

        timerText.setText("Time: " + clockFormat.format(hoursToDisplay)
                + ":" + clockFormat.format(minutesToDisplay)
                + ":" + clockFormat.format(secondsToDisplay));
    }

    public void updateAccumulatedStepCount(SensorEvent event){
        currentStepCount = event.values[0];
        if (initialStepCount == -1){
            initialStepCount = currentStepCount;
        }
        accumulatedStepCount = currentStepCount - initialStepCount;
        stepCountText.setText("Total steps: " + (int)accumulatedStepCount);
    }

    public void updateStepCountRate(){
        stepCountCounter++;
        if (stepCountCounter == 1){
            firstStepCount = currentStepCount;
        }
        else if (stepCountCounter >= 100){ // Add data every 10 sec
            lastStepCount = currentStepCount;
            stepCountRate = lastStepCount - firstStepCount;
            stepCountCounter = 0;
            stepCountRateText.setText("Step-rate: " + (int)stepCountRate);
        }
    }

    public void insertDataAtTimeStamp(long timestamp, float heartRate, float acc_x, float acc_y,
                                      float acc_z, float step_count_rate, float step_count,
                                      short accuracy, @NonNull List<Row> rows) {
        int label = activityToTrack.ordinal();
        Row row = new Row(
                Long.toString(timestamp),
                Float.toString(heartRate),
                Float.toString(acc_x),
                Float.toString(acc_y),
                Float.toString(acc_z),
                Float.toString(step_count_rate),
                Float.toString(step_count),
                Integer.toString(label),
                Short.toString(accuracy)
        );
        rows.add(row);
        dataPointsToAdd += row.toString();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracyReceived) {
        if (sensor == senHeartRateCounter) {
            heartRateAccuracy = (short) accuracyReceived;
        }
    }

    public void onStartButtonClick(View view){
        resetValues();

        dateTime = LocalDateTime.now();

        view.setEnabled(false);
        findViewById(R.id.exitButton).setEnabled(false);
        findViewById(R.id.stopButton).setEnabled(true);
        RadioGroup radioButtons = findViewById(R.id.radioButtonGroup);
        for(int i = 0; i < radioButtons.getChildCount(); i++){
            radioButtons.getChildAt(i).setClickable(false);
        }
        Toast.makeText(getApplicationContext(), "Tracking started for " + activityToTrack.name().toLowerCase(), Toast.LENGTH_SHORT).show();
        registerListeners();
    }

    public void registerListeners(){
        mSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, senHeartRateCounter, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, senStepCounter, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void onStopButtonClick(View view) {
        unregisterListeners();
        findViewById(R.id.stopButton).setEnabled(false);
        findViewById(R.id.startButton).setEnabled(true);
        findViewById(R.id.exitButton).setEnabled(true);

        RadioGroup radioButtons = findViewById(R.id.radioButtonGroup);
        for(int i = 0; i < radioButtons.getChildCount(); i++){
            radioButtons.getChildAt(i).setClickable(true);
        }

        writeToFile(activityToTrack.name().toLowerCase() + "_" + dateTimeFormatter.format(dateTime) + ".csv", dataPointsToAdd);
    }

    public void unregisterListeners(){
        mSensorManager.unregisterListener(this, senAccelerometer);
        mSensorManager.unregisterListener(this, senHeartRateCounter);
        mSensorManager.unregisterListener(this, senStepCounter);
    }

    public void resetValues(){
        dataPointsToAdd = "timestamp,heart_rate,acc_x,acc_y,acc_z,step_count_rate," +
                "step_count,label,heart_rate_accuracy\n";
        rows.clear();
        numberOfDataPointsAdded = 0;
        firstTimestamp = 0;
        timesWrittenToFile = 0;

        // Step count variables
        initialStepCount = -1;
        accumulatedStepCount = 0;
        firstStepCount = 0;
        lastStepCount = 0;
        currentStepCount = 0;
        stepCountRate = 0;
        stepCountCounter = 0;
    }

    public void writeToFile(String fileName, String content){
        Toast.makeText(getApplicationContext(), "Writing to file ...", Toast.LENGTH_SHORT).show();
        File path;
        try {
            path = getApplicationContext().getDir(fileName, Context.MODE_APPEND); // Use MODE_APPEND if you don't want to overwrite the content
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            File file = new File(path.getPath(),fileName);
            file.createNewFile(); // if file already exists, this will do nothing
            FileOutputStream writer = new FileOutputStream(file,true);
            writer.write(content.getBytes());
            writer.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Toast.makeText(getApplicationContext(), "Finished writing to file!", Toast.LENGTH_SHORT).show();
    }

    public void onExitButtonClick(View view) {
        finish();
        System.exit(0);
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

