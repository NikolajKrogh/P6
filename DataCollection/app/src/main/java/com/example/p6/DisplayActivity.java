package com.example.p6;

import static com.example.p6.SelectActivity.getBatteryPercentage;

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

import com.example.p6.databinding.ActivityDisplayBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;



public class DisplayActivity extends Activity implements SensorEventListener, View.OnLongClickListener, View.OnClickListener {

    static class Row{
        String timestamp;
        String minutes;
        String heartRate;
        String step_count;
        String label;

        public Row(String timestamp, String minutes, String heart_rate, String step_count, String label)
        {
            this.timestamp = timestamp;
            this.minutes = minutes;
            this.heartRate = heart_rate;
            this.step_count = step_count;
            this.label = label;
        }
        @NonNull
        @Override
        public String toString(){
            return String.format("%s,%s,%s,%s,%s\n", timestamp, minutes, heartRate, step_count, label);
        }
    }

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
    private List<Row> rows = new ArrayList();
    private short numberOfDataPointsAdded = 0;
    private String dataPointsToAdd = "timestamp,minutes,heart_rate,step_count,label\n";
    //endregion

    //region Formatters
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss");
    private final DecimalFormat decimalFormat = new DecimalFormat("#0.00");
    private final DecimalFormat clockFormat = new DecimalFormat("#00");

    //endregion

    //region Other global variables
    private SensorManager mSensorManager;
    private SelectActivity.Activity activityToTrack = SelectActivity.Activity.WALKING;
    private LocalDateTime dateTime;
    private TextView timerText;
    private TextView timesWrittenToFileText;
    private int timesWrittenToFile = 0;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        retrieveDataFromPreviousActvity();

        getSensors();
        bindTextToVariables();
        dateTime = LocalDateTime.now();
        registerListeners();

        Button stopButton = findViewById(R.id.stopButton);
        stopButton.setOnClickListener(DisplayActivity.this);
        stopButton.setOnLongClickListener(DisplayActivity.this);
    }

    @Override
    public void onUserInteraction(){
        setScreenBrightness(HIGH_BRIGHTNESS);
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(this, "Press and hold to stop", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onLongClick(View v) {
        Log.i("BatteryLevelStop",Integer.toString(getBatteryPercentage(getApplicationContext())));
        stopActivity();
        return false;
    }

    public void setScreenBrightness(float brightness){
        WindowManager.LayoutParams WMLP = getWindow().getAttributes();
        WMLP.screenBrightness = brightness;
        getWindow().setAttributes(WMLP);
    }

    public void retrieveDataFromPreviousActvity(){
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras != null){
            int activityOrdinal = extras.getInt("activityToTrack");
            activityToTrack = SelectActivity.Activity.values()[activityOrdinal];
        }
    }

    // Make back button act as home button
    public void onBackPressed() {
        moveTaskToBack(false);
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
                    insertDataAtTimeStamp(currentTimestamp, minutesSinceStart, heartRate, accumulatedStepCount, rows);
                    numberOfDataPointsAdded++;
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

        Intent intent = new Intent(DisplayActivity.this, SelectActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

    public void insertDataAtTimeStamp(long timestamp, long minutes, float heartRate, float step_count, @NonNull List<Row> rows) {
        int label = activityToTrack.ordinal();
        Row row = new Row(
                Long.toString(timestamp),
                Long.toString(minutes),
                Float.toString(heartRate),
                Float.toString(step_count),
                Integer.toString(label)
        );
        rows.add(row);
        dataPointsToAdd += row.toString();
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
