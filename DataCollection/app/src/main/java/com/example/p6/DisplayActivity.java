package com.example.p6;

import static com.example.p6.MainActivity.Mode.*;
import static com.example.p6.MainActivity.Screen.*;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.p6.classes.NearestCentroid;
import com.example.p6.databinding.ActivityDisplayBinding;
import com.opencsv.exceptions.CsvValidationException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class DisplayActivity extends Activity implements SensorEventListener, View.OnLongClickListener, View.OnClickListener {
    enum Time {
        MINUTES,
        SECONDS,
        MILLISECONDS,
    }

    //region Time constants
    private static final long MILLISEC_TO_NANOSEC_FACTOR = 1000000;
    private static final long SEC_TO_MILLISEC_FACTOR =  1000;
    private static final long MIN_TO_SEC_FACTOR =  60;
    private static final long HOUR_TO_MIN_FACTOR =  60;
    private static final float LOW_BRIGHTNESS = 0.05F;
    private static final float HIGH_BRIGHTNESS = 1F;
    //endregion

    //region Heart rate variables
    private Sensor senHeartRateCounter;
    private TextView heartRateText;
    private float heartRate = 0;
    private long firstTimestamp = 0;
    private long latestTimestamp = 0;
    //endregion

    //region Step count variables
    private Sensor senStepCounter;
    private TextView stepCountText;
    private float initialStepCount = -1; // Initialized as -1 because the initial step count will
    // never be -1, but it may be 0
    private float accumulatedStepCount = 0;
    private float currentStepCount = 0;
    //endregion

    //region Data point variables
    private short numberOfDataPointsAdded = 0;
    private String dataPointsToAdd = "timestamp,minutes,heart_rate,step_count,label\n";
    //endregion

    //region Formatters
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss");
    private final DecimalFormat clockFormat = new DecimalFormat("#00");

    //endregion

    //region Other global variables
    private MainActivity.Activity activityToTrack = MainActivity.activityToTrack;
    private MainActivity.Mode mode = MainActivity.trackingMode;
    private SensorManager mSensorManager;
    private LocalDateTime dateTime;
    private TextView timerText;
    private TextView timesWrittenToFileText;
    private int timesWrittenToFile = 0;
    private Toast myToast;

    //endregion

    private NearestCentroid nearestCentroid = new NearestCentroid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("Create", "Create");
        MainActivity.currentScreen = DISPLAY;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        getSensors();
        bindTextToVariables();
        dateTime = LocalDateTime.now();
        registerListeners();

        Button stopButton = findViewById(R.id.stopActivityButton);
        stopButton.setOnClickListener(DisplayActivity.this);
        stopButton.setOnLongClickListener(DisplayActivity.this);


        /*if (mode == RUN_MODEL) {
            Context context = getApplicationContext();
            String fileName = "centroids/centroids.csv";
            String filePath = context.getFilesDir() + "/" + fileName;
            File csvFile = new File(filePath);
            if (!csvFile.exists()) {
                nearestCentroid.writeCentroidsToFile(nearestCentroid.generalModelCentroids, context);
            }
        }*/


        myToast = Toast.makeText(getApplicationContext(), null, Toast.LENGTH_SHORT);
    }

    @Override
    public void onUserInteraction(){
        setScreenBrightness(HIGH_BRIGHTNESS);
    }

    // onLongClick() for stopActivityButton
    @Override
    public void onClick(View v) {
        myToast.setText("Press and hold to stop");
        myToast.show();
    }

    // onLongClick() for stopActivityButton
    @Override
    public boolean onLongClick(View v) {
        stopActivity();
        return true;
    }

    public void setScreenBrightness(float brightness){
        WindowManager.LayoutParams WMLP = getWindow().getAttributes();
        WMLP.screenBrightness = brightness;
        getWindow().setAttributes(WMLP);
    }

    public void getSensors(){
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        senHeartRateCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        senStepCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
    }

    public void bindTextToVariables(){
        ActivityDisplayBinding binding = ActivityDisplayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        heartRateText = binding.heartRateText;
        stepCountText = binding.stepCountText;
        timerText = binding.timerText;
        timesWrittenToFileText = binding.timesWrittenToFileText;
    }

    public void registerListeners(){
        mSensorManager.registerListener(this, senHeartRateCounter, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, senStepCounter, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_STEP_COUNTER){
            updateAccumulatedStepCount(event);
        }
        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE && event.values[0] > 0) {
            heartRate = event.values[0];
            heartRateText.setText("Heart Rate: " + heartRate);
            long currentTimestamp = event.timestamp;
            updateTimeSinceStart(currentTimestamp);
            if (currentTimestamp - latestTimestamp > 100 * MILLISEC_TO_NANOSEC_FACTOR) {
                if (numberOfDataPointsAdded < 100){
                    long minutesSinceStart = getTimeSinceStart(currentTimestamp)[Time.MINUTES.ordinal()];
                    insertDataAtTimeStamp(currentTimestamp, minutesSinceStart, heartRate, accumulatedStepCount);
                    numberOfDataPointsAdded++;
                    Log.i("i", String.valueOf(numberOfDataPointsAdded));
                }
                else {
                    writeToFile(activityToTrack.name().toLowerCase() + "_" + dateTimeFormatter.format(dateTime) + ".csv", dataPointsToAdd);
                    timesWrittenToFile++;
                    timesWrittenToFileText.setText("Written to file " + timesWrittenToFile + " times");
                    numberOfDataPointsAdded = 0;
                    setScreenBrightness(LOW_BRIGHTNESS);
                }
                ProgressBar dataPointProgressBar = findViewById(R.id.dataPointProgressBar); // initiate the progress bar
                dataPointProgressBar.setProgress(numberOfDataPointsAdded);
                latestTimestamp = currentTimestamp;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void stopActivity() {
        unregisterListeners();
        writeToFile(activityToTrack.name().toLowerCase() + "_" + dateTimeFormatter.format(dateTime) + ".csv", dataPointsToAdd);

        Intent intent;
        if(mode == MainActivity.Mode.RUN_MODEL){
            intent = new Intent(DisplayActivity.this, MainActivity.class);
            MainActivity.BackButtonPressed = true;
        }
        else {
            intent = new Intent(DisplayActivity.this, SelectActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        finish();
    }

    public void unregisterListeners(){
        mSensorManager.unregisterListener(this, senHeartRateCounter);
        mSensorManager.unregisterListener(this, senStepCounter);
    }

    public void updateAccumulatedStepCount(SensorEvent event){
        currentStepCount = event.values[0];
        if (initialStepCount == -1){
            initialStepCount = currentStepCount;
        }
        accumulatedStepCount = currentStepCount - initialStepCount;
        stepCountText.setText("Total steps: " + (int)accumulatedStepCount);
    }

    public void updateTimeSinceStart(long currentTimestamp){
        if (firstTimestamp == 0){
            firstTimestamp = currentTimestamp;
        }

        long secondsSinceStart = getTimeSinceStart(currentTimestamp)[Time.SECONDS.ordinal()];
        long minutesSinceStart = getTimeSinceStart(currentTimestamp)[Time.MINUTES.ordinal()];
        long hoursToDisplay = minutesSinceStart / HOUR_TO_MIN_FACTOR;
        long minutesToDisplay = minutesSinceStart % 60;
        long secondsToDisplay = secondsSinceStart % 60;

        timerText.setText("Time: " + clockFormat.format(hoursToDisplay)
                + ":" + clockFormat.format(minutesToDisplay)
                + ":" + clockFormat.format(secondsToDisplay));
    }

    public void insertDataAtTimeStamp(long timestamp, long minutes, float heartRate, float step_count) {
        int label = activityToTrack.ordinal();
        List<String> row = Arrays.asList(String.format("%s,%s,%d,%d,%s\n", timestamp, minutes, (long)heartRate,(long)step_count, label));
        dataPointsToAdd += String.join(",",row);
    }

    public void writeToFile(String fileName, String content){
        myToast.setText("Writing to file ...");
        myToast.show();
        File path;
        try {
            path = getApplicationContext().getDir(fileName, Context.MODE_APPEND);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            File file = new File(path.getPath(),fileName);
            file.createNewFile(); // if file already exists, this will do nothing
            FileOutputStream writer = new FileOutputStream(file,true);
            writer.write(content.getBytes());
            writer.close();

            // Resets the data points to add
            dataPointsToAdd = "";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public long[] getTimeSinceStart(long currentTimestamp){
        long[] TimeSinceStart = new long[3];

        long nanosecondsSinceStart = currentTimestamp - firstTimestamp;
        long milliSecondsSinceStart = nanosecondsSinceStart / MILLISEC_TO_NANOSEC_FACTOR;
        long secondsSinceStart = milliSecondsSinceStart / SEC_TO_MILLISEC_FACTOR;
        long minutesSinceStart = secondsSinceStart / MIN_TO_SEC_FACTOR;

        TimeSinceStart[Time.MINUTES.ordinal()] = minutesSinceStart;
        TimeSinceStart[Time.SECONDS.ordinal()] = secondsSinceStart;
        TimeSinceStart[Time.MILLISECONDS.ordinal()] = milliSecondsSinceStart;

        return TimeSinceStart;
    }
}
