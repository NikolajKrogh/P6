package com.example.p6;

import static com.example.p6.MainActivity.Activity.*;
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

import com.example.p6.classes.NearestCentroid;
import com.example.p6.classes.CsvHandler;
import com.example.p6.classes.Row;
import com.example.p6.databinding.ActivityDisplayBinding;
import com.opencsv.exceptions.CsvValidationException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
    private short heartRate = 0;
    private long firstTimestamp = 0;
    private long latestTimestamp = 0;
    //endregion

    //region Step count variables
    private Sensor senStepCounter;
    private TextView stepCountText;
    private int initialStepCount = -1; // Initialized as -1 because the initial step count will
    // never be -1, but it may be 0
    private int accumulatedStepCount = 0;
    private int currentStepCount = 0;
    //endregion

    //region Data point variables
    private short numberOfDataPointsAdded = 0;
    private String dataPointHeaderBeforePreprocessing = "minutes,heart_rate,step_count,label\n";
    private String dataPointHeaderAfterPreprocessing = "session_id,heart_rate,step_count,label\n";
    private List<Row> dataPointsToAddArray = new ArrayList<>();
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
    private TextView activityText;

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
            double[] newCentroid = {70.02328727800564, 0.0, 0, 100};

            double[] updatedCentroid = nearestCentroid.updateModel(nearestCentroid.generalModelCentroids[1], newCentroid);

            //Converts matrix to string
            String stringFormattedCentroids = nearestCentroid.multiDimensionalArrayToString(updatedCentroid);

            showToast();
            //Write the new centroids to file
            CsvHandler.writeToFile("centroids" + ".csv", stringFormattedCentroids, context);
            // Resets the data points to add
            dataPointsToAddArray.clear();
        }

        setActivityToTrack();
        activityText.setText("Tracking " + activityToTrack);

        myToast = Toast.makeText(getApplicationContext(), null, Toast.LENGTH_SHORT);

        if (mode == PREDICT_ACTIVITY || mode == UPDATE_WITH_LABELS) {
            String fileName = "centroids.csv";
            String filePath = context.getFilesDir() + "/" + fileName;
            File csvFile = new File(filePath);
            if (!csvFile.exists()) {
                nearestCentroid.writeCentroidsToFile(nearestCentroid.generalModelCentroids, context);
            }
        }


        setActivityToTrack();
        activityText.setText("Tracking \"" + activityToTrack + "\"");


    myToast = Toast.makeText(getApplicationContext(), null, Toast.LENGTH_SHORT);
    }
    public void showToast(){
        myToast.setText("Writing to file ...");
        myToast.show();
    }
    public void setActivityToTrack(){
        switch(mode){
            case PREDICT_ACTIVITY:
                activityToTrack = UNLABELED;
                break;
            case UPDATE_WITH_LABELS:
            case COLLECT_DATA:
                if (activityToTrack == UNLABELED)
                    activityToTrack = WALKING;
                break;
        }

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
        try {
            stopActivity();
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        activityText = binding.activityText;
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
        Context context = getApplicationContext();

        if(event.sensor.getType() == Sensor.TYPE_STEP_COUNTER){
            updateAccumulatedStepCount(event);
        }
        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE && event.values[0] > 0) {
            heartRate = (short)event.values[0];
            heartRateText.setText("Heart Rate: " + heartRate);
            long currentTimestamp = event.timestamp;
            updateTimeSinceStart(currentTimestamp);
            if (currentTimestamp - latestTimestamp > 100 * MILLISEC_TO_NANOSEC_FACTOR) {
                if (numberOfDataPointsAdded < 100){
                    short minutesSinceStart = (short)getTimeSinceStart(currentTimestamp)[Time.MINUTES.ordinal()];
                    addDataPointToArray(minutesSinceStart, heartRate, accumulatedStepCount);
                    numberOfDataPointsAdded++;
                }
                else {
                    switch (mode){
                        case PREDICT_ACTIVITY:     addDataPointsToCorrespondingFile();     break;
                        case UPDATE_WITH_LABELS:                                           break;
                        case COLLECT_DATA:  addDataPointsToFile();                  break;
                    }
                    setScreenBrightness(LOW_BRIGHTNESS);
                }
                ProgressBar dataPointProgressBar = findViewById(R.id.dataPointProgressBar); // initiate the progress bar
                dataPointProgressBar.setProgress(numberOfDataPointsAdded);
                latestTimestamp = currentTimestamp;
            }
        }
    }

    private void addDataPointsToCorrespondingFile(){
        preprocessData();
    }

    private void preprocessData(){

    }

    private void addDataPointsToFile(){
        writeToFile(activityToTrack.name().toLowerCase() + "_" + dateTimeFormatter.format(dateTime) + ".csv", dataPointsToAddArray);
        timesWrittenToFile++;
        timesWrittenToFileText.setText("Written to file " + timesWrittenToFile + " times");
        numberOfDataPointsAdded = 0;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void stopActivity() throws CsvValidationException, IOException {
        Context context = getApplicationContext();
        unregisterListeners();
        if (mode == COLLECT_DATA)
            writeToFile(activityToTrack.name().toLowerCase() + "_" + dateTimeFormatter.format(dateTime) + ".csv", dataPointsToAddArray);
        if (mode == PREDICT_ACTIVITY || mode == UPDATE_WITH_LABELS) {
            //do preprocessing
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
            nearestCentroid.getCentroidsFromFile(context); //this saves the centroids to the nearestCentroid.centroids
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
        currentStepCount = (int)event.values[0];
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

    public void addDataPointToArray(short minutes, short heartRate, int step_count) {
        byte label = (byte)activityToTrack.ordinal();
        Row rowToAdd = new Row(heartRate, step_count, label, minutes);
        dataPointsToAddArray.add(rowToAdd);
    }

    public void writeToFile(String fileName, List<Row> dataPoints){
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
            if (file.length() == 0){
                writer.write(dataPointHeaderBeforePreprocessing.getBytes());
            }
            for (Row dataPoint : dataPoints
                 ) {
                writer.write(dataPoint.toString().getBytes());
            }
            writer.close();

            // Resets the data points to add
            dataPointsToAddArray.clear();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int[] getTimeSinceStart(long currentTimestamp){
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
