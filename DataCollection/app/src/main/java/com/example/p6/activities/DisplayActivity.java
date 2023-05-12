package com.example.p6.activities;

import static com.example.p6.classes.Constants.Mode.*;
import static com.example.p6.classes.Constants.Screen.*;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.p6.R;
import com.example.p6.classes.AccuracyData;
import com.example.p6.classes.Constants;
import com.example.p6.handlers.CsvHandler;
import com.example.p6.classes.DataPointRaw;
import com.example.p6.handlers.NearestCentroidHandler;
import com.example.p6.handlers.PreProcessingHandler;
import com.example.p6.databinding.ActivityDisplayBinding;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class DisplayActivity extends Activity implements SensorEventListener, View.OnLongClickListener, View.OnClickListener {

    private android.content.Context context;

    private enum Time {
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
    private static final short NUMBER_OF_DATA_POINTS_LIMIT = 100;
    //endregion

    //region Heart rate variables
    private Sensor senHeartRateCounter;
    private TextView heartRateText;
    private long firstTimestamp = 0;
    private long latestTimestamp = 0;
    //endregion

    //region Step count variables
    private Sensor senStepCounter;
    private TextView stepCountText;
    private int initialStepCount = -1; // Initialized as -1 because the initial step count will
    // never be -1, but it may be 0
    private int accumulatedStepCount = 0;
    //endregion

    //region Data point variables
    private short numberOfDataPointsAdded = 0;
    private List<DataPointRaw> dataPointsToAdd = new ArrayList<>();
    //endregion

    //region Other global variables
    private final Constants.Activity activityToTrack = MainActivity.activityToTrack;
    private final Constants.Mode mode = MainActivity.trackingMode;
    private SensorManager mSensorManager;
    private LocalDateTime dateTime;
    private TextView timerText;
    private TextView timesWrittenToFileText;
    private static TextView predictedActivityText;
    private short timesWrittenToFile = 0;
    private TextView activityText;
    Intent batteryStatus;

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MainActivity.currentScreen = DISPLAY;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        context = getApplicationContext();

        Locale.setDefault(Locale.US);   // Makes String.Format language-insensitive

        try {
            NearestCentroidHandler.centroids = CsvHandler.getCentroidsFromFile(context);
        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException(e);
        }

        getSensors();
        bindTextToVariables();
        dateTime = LocalDateTime.now();
        registerListeners();

        Button stopButton = findViewById(R.id.stopActivityButton);
        stopButton.setOnClickListener(DisplayActivity.this);
        stopButton.setOnLongClickListener(DisplayActivity.this);

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        batteryStatus = context.registerReceiver(null, ifilter);

        activityText.setText("Tracking " + activityToTrack);
    }

    @Override
    public void onUserInteraction(){
        setScreenBrightness(HIGH_BRIGHTNESS);
    }

    // onLongClick() for stopActivityButton
    @Override
    public void onClick(View v) {
        MainActivity.showToast("Press and hold to stop");
    }

    // onLongClick() for stopActivityButton
    @Override
    public boolean onLongClick(View v) {
        try {
            stopActivity();
        } catch (CsvValidationException | IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    private void setScreenBrightness(float brightness){
        WindowManager.LayoutParams WMLP = getWindow().getAttributes();
        WMLP.screenBrightness = brightness;
        getWindow().setAttributes(WMLP);
    }

    private void getSensors(){
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        senHeartRateCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        senStepCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
    }

    private void bindTextToVariables(){
        ActivityDisplayBinding binding = ActivityDisplayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activityText = binding.activityText;
        heartRateText = binding.heartRateText;
        stepCountText = binding.stepCountText;
        timerText = binding.timerText;
        predictedActivityText = binding.predictedActivityText;
        timesWrittenToFileText = binding.timesWrittenToFileText;
    }

    private void registerListeners(){
        mSensorManager.registerListener(this, senHeartRateCounter, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, senStepCounter, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_STEP_COUNTER){
            updateAccumulatedStepCount(event);
        }
        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE && event.values[0] > 0) {
            short heartRate = (short) event.values[0];
            heartRateText.setText("Heart Rate: " + heartRate);
            long currentTimestamp = event.timestamp;
            updateTimeSinceStart(currentTimestamp);
            if (currentTimestamp - latestTimestamp > 100 * MILLISEC_TO_NANOSEC_FACTOR) {
                if (numberOfDataPointsAdded < NUMBER_OF_DATA_POINTS_LIMIT){
                    short minutesSinceStart = (short)getTimeSinceStart(currentTimestamp)[Time.MINUTES.ordinal()];
                    addDataPointToArray(minutesSinceStart, heartRate, accumulatedStepCount);
                    numberOfDataPointsAdded++;
                }
                else {

                    if (getBatteryLevel() <= 5){
                        try {
                            stopActivity();
                        } catch (CsvValidationException | IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    switch (mode){
                        case PREDICT_ACTIVITY:
                        case UPDATE_WITH_LABELS:
                        case TEST_ACCURACY:
                            PreProcessingHandler.addAggregatedDataPointsToCorrespondingList(dataPointsToAdd);
                            break;
                        case COLLECT_DATA:
                            CsvHandler.writeDataPointsToFile(activityToTrack.name().toLowerCase() + "_" +
                                    Constants.dateTimeFormatter.format(dateTime) + ".csv", dataPointsToAdd, context);
                            break;
                        default:
                            throw new RuntimeException("Mode " + mode + " not recognized");
                    }

                    timesWrittenToFile++;
                    timesWrittenToFileText.setText("Written to file " + timesWrittenToFile + " times");
                    numberOfDataPointsAdded = 0;
                    dataPointsToAdd.clear();
                    setScreenBrightness(LOW_BRIGHTNESS);
                }
                ProgressBar dataPointProgressBar = findViewById(R.id.dataPointProgressBar); // initiate the progress bar
                dataPointProgressBar.setProgress(numberOfDataPointsAdded);
                latestTimestamp = currentTimestamp;
            }
        }
    }

    // Function taken from:
    // https://developer.android.com/training/monitoring-device-state/battery-monitoring
    private double getBatteryLevel() {
        // Scale is the maximum battery percentage
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);

        return level * 100 / (double) scale;
    }

    public static void displayPredictedActivity(Constants.Activity activity){
        predictedActivityText.setText("Predicted " + activity.name());
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void stopActivity() throws CsvValidationException, IOException {
        unregisterListeners();
        Intent intent = new Intent(DisplayActivity.this, SelectActivity.class);
        AccuracyData accuracyDataForActivity = new AccuracyData();

        if (mode == UPDATE_WITH_LABELS || mode == TEST_ACCURACY) {
             accuracyDataForActivity = new AccuracyData(PreProcessingHandler.predictedActivities, activityToTrack);
        }

        switch (mode){
            case PREDICT_ACTIVITY:
                PreProcessingHandler.updateModelForPredictedActivities(dataPointsToAdd, context);
                intent = new Intent(DisplayActivity.this, MainActivity.class);
                MainActivity.BackButtonPressed = true;
                MainActivity.showToast("Updated model based on predictions");
                break;
            case UPDATE_WITH_LABELS:
                PreProcessingHandler.updateModelForPredictedActivities(dataPointsToAdd, context);
                CsvHandler.writeToAccuracyFileForActivity(accuracyDataForActivity, context);
                CsvHandler.writeToTotalAccuracyFileForActivity(accuracyDataForActivity, context);
                MainActivity.showToast("Updated model for " + activityToTrack);
                break;
            case TEST_ACCURACY:
                PreProcessingHandler.addAggregatedDataPointsToCorrespondingList(dataPointsToAdd);
                CsvHandler.writeToAccuracyFileForActivity(accuracyDataForActivity, context);
                MainActivity.showToast("Accuracy calculated for " + activityToTrack);
                break;
            case COLLECT_DATA:
                MainActivity.showToast("Wrote to file " + activityToTrack);
                String fileName = activityToTrack.name().toLowerCase() + "_" +
                        Constants.dateTimeFormatter.format(dateTime) + ".csv";
                CsvHandler.writeDataPointsToFile(fileName, dataPointsToAdd, context);
                break;
            default:
                throw new RuntimeException("Mode " + mode + " not recognized");
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        finish();
    }

    private void unregisterListeners(){
        mSensorManager.unregisterListener(this, senHeartRateCounter);
        mSensorManager.unregisterListener(this, senStepCounter);
    }

    private void updateAccumulatedStepCount(SensorEvent event){
        int currentStepCount = (int) event.values[0];
        if (initialStepCount == -1){
            initialStepCount = currentStepCount;
        }
        accumulatedStepCount = currentStepCount - initialStepCount;
        stepCountText.setText("Total steps: " + accumulatedStepCount);
    }

    private void updateTimeSinceStart(long currentTimestamp){
        if (firstTimestamp == 0){
            firstTimestamp = currentTimestamp;
        }

        long secondsSinceStart = getTimeSinceStart(currentTimestamp)[Time.SECONDS.ordinal()];
        long minutesSinceStart = getTimeSinceStart(currentTimestamp)[Time.MINUTES.ordinal()];
        long hoursToDisplay = minutesSinceStart / HOUR_TO_MIN_FACTOR;
        long minutesToDisplay = minutesSinceStart % 60;
        long secondsToDisplay = secondsSinceStart % 60;

        timerText.setText("Time: " + Constants.clockFormat.format(hoursToDisplay)
                + ":" + Constants.clockFormat.format(minutesToDisplay)
                + ":" + Constants.clockFormat.format(secondsToDisplay));
    }

    private void addDataPointToArray(short minutes, short heartRate, int step_count) {
        byte label = (byte)activityToTrack.ordinal();
        DataPointRaw dataPointToAdd = new DataPointRaw(heartRate, step_count, label, minutes);
        dataPointsToAdd.add(dataPointToAdd);
    }

    private int[] getTimeSinceStart(long currentTimestamp) {
        int[] TimeSinceStart = new int[3];

        long nanosecondsSinceStart = currentTimestamp - firstTimestamp;
        long milliSecondsSinceStart = nanosecondsSinceStart / MILLISEC_TO_NANOSEC_FACTOR;
        int secondsSinceStart = (int)(milliSecondsSinceStart / SEC_TO_MILLISEC_FACTOR);
        int minutesSinceStart = (int)(secondsSinceStart / MIN_TO_SEC_FACTOR);

        TimeSinceStart[Time.MINUTES.ordinal()] = minutesSinceStart;
        TimeSinceStart[Time.SECONDS.ordinal()] = secondsSinceStart;
        TimeSinceStart[Time.MILLISECONDS.ordinal()] = (int)milliSecondsSinceStart;

        return TimeSinceStart;
    }
}
