package com.example.p6.activities;

import static com.example.p6.classes.Constants.Activity.*;
import static com.example.p6.classes.Constants.Screen.*;
import static com.example.p6.classes.Constants.Mode.*;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.p6.R;
import com.example.p6.classes.AccuracyData;
import com.example.p6.classes.Constants;
import com.example.p6.classes.DataPointRaw;
import com.example.p6.handlers.CsvHandler;
import com.example.p6.databinding.ActivityMainBinding;
import com.example.p6.handlers.NearestCentroidHandler;
import com.example.p6.handlers.PreProcessingHandler;
import com.opencsv.exceptions.CsvValidationException;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainActivity extends Activity implements View.OnLongClickListener, View.OnClickListener {

    private android.content.Context context;

    //region Global variables
    public static Constants.Activity activityToTrack = UNLABELED;
    static Constants.Screen currentScreen = MAIN;
    public static Constants.Mode trackingMode = COLLECT_DATA;
    static boolean BackButtonPressed = false;
    //endregion

    static public Toast myToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = getApplicationContext();

        Button resetButton = findViewById(R.id.resetModelButton);
        resetButton.setOnClickListener(MainActivity.this);
        resetButton.setOnLongClickListener(MainActivity.this);

        myToast = Toast.makeText(context, null, Toast.LENGTH_SHORT);
    }

    @Override
    public void onRestart(){
        super.onRestart();
        if (!BackButtonPressed && currentScreen != MAIN){   // If we came from the home menu of the watch
            startCurrentlyRunningActivity();
        }
        currentScreen = MAIN;
        BackButtonPressed = false;
    }

    private void goToScreen(Class activityClass){
        Intent intent = new Intent(MainActivity.this, activityClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    private void startCurrentlyRunningActivity(){
        switch (currentScreen){
            case SELECT:
                goToScreen(SelectActivity.class);
                break;
            case DISPLAY:
                goToScreen(DisplayActivity.class);
                break;
            case VIEW_MODEL:
                goToScreen(ViewModelActivity.class);
                break;
            default:
                throw new RuntimeException("Screen " + currentScreen + " not recognized");
        }
    }

    public void onPredictActivityClick(View view){
        activityToTrack = UNLABELED;
        trackingMode = PREDICT_ACTIVITY;
        goToScreen(DisplayActivity.class);
    }

    public void onUpdateWithLabelsClick(View view){
        trackingMode = UPDATE_WITH_LABELS;
        goToScreen(SelectActivity.class);
    }

    public void onCollectDataClick(View view){
        trackingMode = COLLECT_DATA;
        goToScreen(SelectActivity.class);
    }

    public void onTestAccuracyClick(View view){
        trackingMode = TEST_ACCURACY;
        goToScreen(SelectActivity.class);
    }

    public void onUpdateModelClick(View view) throws CsvValidationException, IOException {
        trackingMode = UPDATE_MODEL;
        NearestCentroidHandler.centroids = CsvHandler.getCentroidsFromFile(context);
        String [] files = context.getAssets().list("");

        for (String fileName : files) {
            if (fileName.endsWith(".csv")) {
                setActivityToTrackBasedOnFileName(fileName);
                List<DataPointRaw> rawDataPoints = CsvHandler.getDataPointsFromFile(context, fileName);
                PreProcessingHandler.updateModelForPredictedActivities(rawDataPoints, context);
                CsvHandler.writeToTotalAccuracyFileForActivity(new AccuracyData(PreProcessingHandler.predictedActivities, activityToTrack), context);
            }
        }

        DisplayActivity.showToast("Model was updated");
    }

    private void setActivityToTrackBasedOnFileName(String fileName){
        if (fileName.contains("sitting")) {
            activityToTrack = SITTING;
        }
        else if (fileName.contains("walking")) {
            activityToTrack = WALKING;
        }
        else if (fileName.contains("running")) {
            activityToTrack = RUNNING;
        }
        else if (fileName.contains("cycling")) {
            activityToTrack = CYCLING;
        }
    }

    public void onViewModelClick(View view){
        goToScreen(ViewModelActivity.class);
    }

    // onClick() for resetModelButton
    @Override
    public void onClick(View v) {
        myToast.setText("Press and hold to reset");
        myToast.show();
    }

    public void onExitButtonClick(View view){
        finishAndRemoveTask();
        System.exit(0);
    }

    // onLongClick() for resetModelButton
    @Override
    public boolean onLongClick(View v) {
        CsvHandler.deleteFile("centroids.csv", context);
        CsvHandler.deleteFile("centroids_history.csv", context);
        CsvHandler.deleteFile("accuracy_total_for_sitting.csv", context);
        CsvHandler.deleteFile("accuracy_total_for_walking.csv", context);
        CsvHandler.deleteFile("accuracy_total_for_running.csv", context);
        CsvHandler.deleteFile("accuracy_total_for_cycling.csv", context);

        myToast.setText("Model has been reset");
        myToast.show();

        return true;    // true means that the long click is "consumed"
    }
}