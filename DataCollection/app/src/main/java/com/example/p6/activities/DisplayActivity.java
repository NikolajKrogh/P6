package com.example.p6.activities;

import static com.example.p6.classes.Constants.Mode.*;
import static com.example.p6.classes.Constants.Screen.*;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.provider.ContactsContract;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    private static final short NUMBER_OF_DATA_POINTS_LIMIT = 1000;
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


    //endregion

    //region Formatters
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss");
    private final DecimalFormat clockFormat = new DecimalFormat("#00");

    //endregion

    //region Other global variables
    private Constants.Activity activityToTrack = MainActivity.activityToTrack;
    private final Constants.Mode mode = MainActivity.trackingMode;
    private SensorManager mSensorManager;
    private LocalDateTime dateTime;
    private TextView timerText;
    private TextView timesWrittenToFileText;
    private short timesWrittenToFile = 0;
    private Toast myToast;
    private TextView activityText;
    private String sessionId;

    //endregion

    private NearestCentroid nearestCentroid = new NearestCentroid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Context context = getApplicationContext();
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

        myToast = Toast.makeText(getApplicationContext(), null, Toast.LENGTH_SHORT);


        //Run Model page
        if(mode == PREDICT_ACTIVITY){
            // Collect data from sensors


            // On every x number of sensorUpdate, run nearestCentroidAlgorithm on preprocessed data.
            /*
            if (personalizedModel.length != 0) {

            }else{

            }

             */
            DataPoint vectorToAddToCentroid = new DataPoint((short) 160, 160, (byte) 0, (short) 1);
            Constants.Activity nearestCentroidLabel = nearestCentroid.predict(
                                                            vectorToAddToCentroid, nearestCentroid.generalModelCentroids);

            showToast("Predicted activity: " + nearestCentroidLabel.name());
            //model[closestCentroidIndex] = updateModel(model[closestCentroidIndex], (Row) vectorToAddToCentroid);
        }

        activityText.setText("Tracking " + activityToTrack);

        if (mode == PREDICT_ACTIVITY || mode == UPDATE_WITH_LABELS) {
            String fileName = "centroids.csv";
            File file = new File(context.getFilesDir(), fileName);
            if (!file.exists()) {
                String content = Constants.centroidHeader;
                content += CsvHandler.convertArrayOfCentroidsToString(nearestCentroid.generalModelCentroids,"\n");
                CsvHandler.writeToFile(fileName,content,context,false);
            }
        }
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
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
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
        timesWrittenToFileText = binding.timesWrittenToFileText;
    }

    private void registerListeners(){
        mSensorManager.registerListener(this, senHeartRateCounter, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, senStepCounter, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onSensorChanged(SensorEvent event) {
        Context context = getApplicationContext();

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
                            addDataPointsToCorrespondingFile();
                            break;
                        case UPDATE_WITH_LABELS:
                            PreProcessing.makeBudgetTimeSeries(dataPointsToAdd, sessionId);
                            break;
                        case COLLECT_DATA:
                            writeToFile(activityToTrack.name().toLowerCase() + "_" +
                                    dateTimeFormatter.format(dateTime) + ".csv", dataPointsToAdd);
                            break;
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

    private void addDataPointsToCorrespondingFile(){
        PreProcessing.makeBudgetTimeSeries(dataPointsToAdd, sessionId);
        for (DataPoint dataPoint : PreProcessing.aggregatedDataPoints) {
            Constants.Activity predictedActivity = NearestCentroid.predict(dataPoint, NearestCentroid.centroids);

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
            }
        }
        PreProcessing.aggregatedDataPoints.clear();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void stopActivity() throws CsvValidationException, IOException {
        Context context = getApplicationContext();
        unregisterListeners();
        if (mode == COLLECT_DATA)
            writeToFile(activityToTrack.name().toLowerCase() + "_" + dateTimeFormatter.format(dateTime) + ".csv", dataPointsToAdd);
        if (mode == PREDICT_ACTIVITY || mode == UPDATE_WITH_LABELS) {
            PreProcessing.makeBudgetTimeSeries(dataPointsToAdd, sessionId);
        }
        if (mode == PREDICT_ACTIVITY) {
            /*
            SitRows;
            walkRows;
            runRows;
            cycleRows;

            for row in rows:
                returnedRow = runModel(row) //this returned row should contain a label for the row
                put returned row i den rigtige XXRows (baseret på dens label)
            for hver af de 4 XXRows:
                append til hver deres fil (sitting.csv, walking.csv, etc...)
                Hvis det at vi appender til filen betyder at filen har mindst X rækker:
                    opdaterer modellen
             */
        }
        else if (mode == UPDATE_WITH_LABELS) {
            NearestCentroid.centroids = CsvHandler.getCentroidsFromFile(context);
            //double[][] newDataPoints = new double[][](); //this should be a list/array of rows, and should be made on the go
            //it should also be the processed data

            //double[][] updatedCentroids = nearestCentroid.updateModel(nearestCentroid.centroids, newDataPoints);

            //Write the new centroids to file
            //nearestCentroid.writeCentroidsToFile(updatedCentroids,context);
        }
        Intent intent;
        if(mode == PREDICT_ACTIVITY){
            intent = new Intent(DisplayActivity.this, MainActivity.class);
            MainActivity.BackButtonPressed = true;

            // Collect data from sensors


            // On every x number of sensorUpdate, run nearestCentroidAlgorithm on preprocessed data.
            /*
            if(){

            }
             */

            // Display the predicted activity

        }
        else {
            intent = new Intent(DisplayActivity.this, SelectActivity.class);
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
        stepCountText.setText("Total steps: " + (int)accumulatedStepCount);
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

    private void writeToFile(String fileName, List<DataPoint> dataPoints) {
        showToast("Writing to file ...");
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
            if (file.length() == 0){
                String dataPointHeaderBeforePreprocessing = "minutes,heart_rate,step_count,label\n";
                writer.write(dataPointHeaderBeforePreprocessing.getBytes());
            }
            for (DataPoint dataPoint : dataPoints
                 ) {
                writer.write(dataPoint.toString().getBytes());
            }
            writer.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
