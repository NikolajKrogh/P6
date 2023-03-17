package com.example.p6;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
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

public class DisplayActivity extends Activity implements SensorEventListener {

    // Class for writing data to a row; representing a data point
    static class Row{
        String timestamp;
        String minutes;
        String heartRate;
        String acc_x;
        String acc_y;
        String acc_z;
        String step_count;
        String label;

        public Row(String timestamp, String minutes, String heart_rate,
                   String acc_x, String acc_y, String acc_z, String step_count, String label)
        {
            this.timestamp = timestamp;
            this.minutes = minutes;
            this.heartRate = heart_rate;
            this.acc_x = acc_x;
            this.acc_y = acc_y;
            this.acc_z = acc_z;
            this.step_count = step_count;
            this.label = label;
        }
        @NonNull
        @Override
        public String toString(){
            return String.format("%s,%s,%s,%s,%s,%s,%s,%s\n",
                    timestamp, minutes, heartRate, acc_x, acc_y, acc_z, step_count, label);
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
    //endregion

    //region Accelerometer variables
    private Sensor senAccelerometer;
    private TextView accelerometerText;
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
    //endregion

    //region Data point variables
    private final List<Row> rows = new ArrayList();
    private short numberOfDataPointsAdded = 0;
    private String dataPointsToAdd = "timestamp,minutes,heart_rate,acc_x,acc_y,acc_z," +
            "step_count,label,heart_rate_accuracy\n";
    //endregion

    //region Formatters
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");
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

        // retrieve the data from previous activity
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras != null){
            int activityOrdinal = extras.getInt("activityToTrack");
            activityToTrack = SelectActivity.Activity.values()[activityOrdinal];
        }

        getSensors();
        bindTextToVariables();
        dateTime = LocalDateTime.now();
        registerListeners();
    }

    public void getSensors(){
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        senAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senHeartRateCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        senStepCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
    }

    public void bindTextToVariables(){
        ActivityDisplayBinding binding = ActivityDisplayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        accelerometerText = binding.accelerometerText;
        heartRateText = binding.heartRateText;
        stepCountText = binding.stepCountText;
        timerText = binding.timerText;
        timesWrittenToFileText = binding.timesWrittenToFileText;
    }

    public void registerListeners(){
        mSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, senHeartRateCounter, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, senStepCounter, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void unregisterListeners(){
        mSensorManager.unregisterListener(this, senAccelerometer);
        mSensorManager.unregisterListener(this, senHeartRateCounter);
        mSensorManager.unregisterListener(this, senStepCounter);
    }

    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_STEP_COUNTER){
            updateAccumulatedStepCount(event);
        }
        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE && event.values[0] > 0) {
            heartRate = event.values[0];
            heartRateText.setText("Heart Rate: " + heartRate);
        }
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && heartRate > 0) {
            long currentTimestamp = event.timestamp;
            updateTimer(currentTimestamp);
            if (currentTimestamp - latestTimestamp > 100 * MILLISEC_TO_NANOSEC_FACTOR) {
                float x_axis = event.values[0];
                float y_axis = event.values[1];
                float z_axis = event.values[2];
                if (numberOfDataPointsAdded <= 500){
                    long minutesSinceStart = getTimeSinceStart(currentTimestamp)[Time.MINUTES.ordinal()];
                    insertDataAtTimeStamp(currentTimestamp, minutesSinceStart, heartRate, x_axis,
                            y_axis, z_axis,  accumulatedStepCount, rows);
                }
                else {
                    writeToFile(activityToTrack.name().toLowerCase() + "_" + dateTimeFormatter.format(dateTime) + ".csv", dataPointsToAdd);
                }
                ProgressBar dataPointProgressBar = findViewById(R.id.dataPointProgressBar);
                dataPointProgressBar.setProgress(numberOfDataPointsAdded);
                accelerometerText.setText("x: " + decimalFormat.format(x_axis)
                        + "   y: " + decimalFormat.format(y_axis)
                        + "   z: " + decimalFormat.format(z_axis));
                latestTimestamp = currentTimestamp;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing
    }

    public void onStopButtonClick(View view) {
        unregisterListeners();
        writeToFile(activityToTrack.name().toLowerCase() + "_"
                + dateTimeFormatter.format(dateTime) + ".csv", dataPointsToAdd);

        // Launch new activity and clear old from history
        Intent intent = new Intent(DisplayActivity.this, SelectActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void updateAccumulatedStepCount(SensorEvent event){
        float currentStepCount = event.values[0];
        if (initialStepCount == -1){
            initialStepCount = currentStepCount;
        }
        accumulatedStepCount = currentStepCount - initialStepCount;
        stepCountText.setText("Total steps: " + (int)accumulatedStepCount);
    }

    public void updateTimer(long currentTimestamp){
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

    // Returns array [minutes, seconds, milliseconds] of how much time has passed since start
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

    public void insertDataAtTimeStamp(long timestamp, long minutes, float heartRate, float acc_x, float acc_y,
                                      float acc_z, float step_count, @NonNull List<Row> rows) {
        int label = activityToTrack.ordinal();
        Row row = new Row(
                Long.toString(timestamp),
                Long.toString(minutes),
                Float.toString(heartRate),
                Float.toString(acc_x),
                Float.toString(acc_y),
                Float.toString(acc_z),
                Float.toString(step_count),
                Integer.toString(label)
        );
        rows.add(row);
        dataPointsToAdd += row.toString();
        numberOfDataPointsAdded++;
    }

    public void writeToFile(String fileName, String content){
        Toast.makeText(getApplicationContext(), "Writing to file ...", Toast.LENGTH_SHORT).show();
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

            timesWrittenToFile++;
            timesWrittenToFileText.setText("Written to file " + timesWrittenToFile + " times");
            dataPointsToAdd = "";
            numberOfDataPointsAdded = 0;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}