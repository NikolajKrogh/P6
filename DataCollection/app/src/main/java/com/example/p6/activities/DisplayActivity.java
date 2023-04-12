package com.example.p6.activities;

import static com.example.p6.classes.Constants.Mode.*;
import static com.example.p6.classes.Constants.Screen.*;

import android.app.Activity;
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

import com.example.p6.R;
import com.example.p6.classes.Constants;
import com.example.p6.classes.CsvHandler;
import com.example.p6.classes.NearestCentroid;
import com.example.p6.classes.DataPoint;
import com.example.p6.classes.PreProcessing;
import com.example.p6.databinding.ActivityDisplayBinding;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;


public class DisplayActivity extends Activity implements SensorEventListener, View.OnLongClickListener, View.OnClickListener {
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
    private List<DataPoint> dataPointsToAdd = new ArrayList<>();
    private List<DataPoint> aggregatedDataPointsSitting = new ArrayList<>();
    private List<DataPoint> aggregatedDataPointsWalking = new ArrayList<>();
    private List<DataPoint> aggregatedDataPointsRunning = new ArrayList<>();
    private List<DataPoint> aggregatedDataPointsCycling = new ArrayList<>();
    private List<String> predictedActivities = new ArrayList<>();


    //endregion

    //region Formatters
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss");
    private final DecimalFormat clockFormat = new DecimalFormat("#00");

    //endregion

    //region Other global variables
    private final Constants.Activity activityToTrack = MainActivity.activityToTrack;
    private final Constants.Mode mode = MainActivity.trackingMode;
    private SensorManager mSensorManager;
    private LocalDateTime dateTime;
    private TextView timerText;
    private TextView timesWrittenToFileText;
    private TextView predictedActivityText;
    private short timesWrittenToFile = 0;
    private Toast myToast;
    private TextView activityText;
    private String sessionId;
    private boolean modelWasUpdated = false;

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MainActivity.currentScreen = DISPLAY;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Locale.setDefault(Locale.US);   // Makes String.Format language-insensitive

        try {
            NearestCentroid.centroids = CsvHandler.getCentroidsFromFile(getApplicationContext());
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

        myToast = Toast.makeText(getApplicationContext(), null, Toast.LENGTH_SHORT);
        activityText.setText("Tracking " + activityToTrack);

        Random rand = new Random();
        sessionId = String.valueOf(rand.nextInt(Integer.MAX_VALUE));

        myToast = Toast.makeText(getApplicationContext(), null, Toast.LENGTH_SHORT);
    }

    private void showToast(String text) {
        myToast.setText(text);
        myToast.show();
    }

    @Override
    public void onUserInteraction(){
        setScreenBrightness(HIGH_BRIGHTNESS);
    }

    // onLongClick() for stopActivityButton
    @Override
    public void onClick(View v) {
        showToast("Press and hold to stop");
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
                    switch (mode){
                        case PREDICT_ACTIVITY:
                        case UPDATE_WITH_LABELS:
                            addDataPointsToCorrespondingList();
                            break;
                        case COLLECT_DATA:
                            CsvHandler.writeDataPointsToFile(activityToTrack.name().toLowerCase() + "_" +
                                    dateTimeFormatter.format(dateTime) + ".csv", dataPointsToAdd, getApplicationContext());
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

    private void addDataPointsToCorrespondingList(){
        PreProcessing.makeBudgetTimeSeries(dataPointsToAdd, sessionId);
        for (DataPoint dataPoint : PreProcessing.aggregatedDataPoints) {
            Constants.Activity predictedActivity = activityToTrack;

            if (mode == PREDICT_ACTIVITY){
                predictedActivity = NearestCentroid.predict(dataPoint, NearestCentroid.centroids);
                predictedActivities.add(predictedActivity.name().toLowerCase());
                predictedActivityText.setText("Predicted " + predictedActivity.name());
            }

            switch (predictedActivity){
                case SITTING:
                    aggregatedDataPointsSitting.add(dataPoint);
                    break;
                case WALKING:
                    aggregatedDataPointsWalking.add(dataPoint);
                    break;
                case RUNNING:
                    aggregatedDataPointsRunning.add(dataPoint);
                    break;
                case CYCLING:
                    aggregatedDataPointsCycling.add(dataPoint);
                    break;
                default:
                    throw new RuntimeException("Activity " + predictedActivity + " not recognized");
            }
        }

        PreProcessing.aggregatedDataPoints.clear();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void stopActivity() throws CsvValidationException, IOException {
        unregisterListeners();
        Intent intent = new Intent(DisplayActivity.this, SelectActivity.class);

        switch (mode){
            case PREDICT_ACTIVITY:
                updateModelForPredictedActivities();
                intent = new Intent(DisplayActivity.this, MainActivity.class);
                MainActivity.BackButtonPressed = true;
                break;
            case UPDATE_WITH_LABELS:
                updateModelForPredictedActivities();
                break;
            case COLLECT_DATA:
                showToast("Writing to file ...");
                CsvHandler.writeDataPointsToFile(activityToTrack.name().toLowerCase() + "_" +
                        dateTimeFormatter.format(dateTime) + ".csv", dataPointsToAdd, getApplicationContext());
                break;
            default:
                throw new RuntimeException("Mode " + mode + " not recognized");
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        finish();
    }

    private void updateModelForPredictedActivities(){
        addDataPointsToCorrespondingList();
        for (short i = 0; i < Constants.NUMBER_OF_LABELS; i++) {
            List<DataPoint> listForActivity = getListForActivity(Constants.Activity.values()[i]);
            updateCentroidForActivity(listForActivity, Constants.Activity.values()[i]);
        }
        if (modelWasUpdated){
            showToast("Updated model");
            CsvHandler.writeToCentroidFile(NearestCentroid.centroids, getApplicationContext());
            CsvHandler.writeToCentroidHistory(NearestCentroid.centroids, getApplicationContext());
        }
    }

    private List<DataPoint> getListForActivity(Constants.Activity activity){
        switch (activity){
            case SITTING:
                return aggregatedDataPointsSitting;
            case WALKING:
                return aggregatedDataPointsWalking;
            case RUNNING:
                return aggregatedDataPointsRunning;
            case CYCLING:
                return  aggregatedDataPointsCycling;
            default:
                throw new RuntimeException("Activity " + activity + " does not correspond to any list");
        }
    }

    private void updateCentroidForActivity(List<DataPoint> aggregatedDataPointsForActivity, Constants.Activity activity){
        for (DataPoint dataPoint : aggregatedDataPointsForActivity) {
            modelWasUpdated = true;
            NearestCentroid.centroids[activity.ordinal()] = NearestCentroid.updateModel(activity, dataPoint);
        }
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

        timerText.setText("Time: " + clockFormat.format(hoursToDisplay)
                + ":" + clockFormat.format(minutesToDisplay)
                + ":" + clockFormat.format(secondsToDisplay));
    }

    private void addDataPointToArray(short minutes, short heartRate, int step_count) {
        byte label = (byte)activityToTrack.ordinal();
        DataPoint dataPointToAdd = new DataPoint(heartRate, step_count, label, minutes);
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
