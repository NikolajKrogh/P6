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
import com.example.p6.classes.Constants;
import com.example.p6.classes.CsvHandler;
import com.example.p6.databinding.ActivityMainBinding;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;

public class MainActivity extends Activity implements View.OnLongClickListener, View.OnClickListener {

    //region Global variables
    static Constants.Activity activityToTrack = UNLABELED;
    static Constants.Screen currentScreen = MAIN;
    static Constants.Mode trackingMode = COLLECT_DATA;
    static boolean BackButtonPressed = false;
    //endregion

    private Toast myToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Button resetButton = findViewById(R.id.resetModelButton);
        resetButton.setOnClickListener(MainActivity.this);
        resetButton.setOnLongClickListener(MainActivity.this);

        myToast = Toast.makeText(getApplicationContext(), null, Toast.LENGTH_SHORT);
    }

    @Override
    public void onRestart(){
        super.onRestart();
        if (!BackButtonPressed && currentScreen != MAIN){   // If we came from the home menu of the watch
            startCurrentlyRunningActivity();
        }
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

    public void onRunModelClick(View view){
        trackingMode = PREDICT_ACTIVITY;
        goToScreen(DisplayActivity.class);
    }

    public void onSynchronizeModelClick(View view){
        trackingMode = UPDATE_WITH_LABELS;
        goToScreen(SelectActivity.class);
    }

    public void onViewModelClick(View view){
        goToScreen(ViewModelActivity.class);
    }

    public void onCollectDataClick(View view){
        trackingMode = COLLECT_DATA;
        goToScreen(SelectActivity.class);
    }

    public void onExitButtonClick(View view){
        finishAndRemoveTask();
        System.exit(0);
    }

    // onClick() for resetModelButton
    @Override
    public void onClick(View v) {
        myToast.setText("Press and hold to reset");
        myToast.show();
    }

    // onLongClick() for resetModelButton
    @Override
    public boolean onLongClick(View v) {
        CsvHandler.deleteFile("centroids.csv", getApplicationContext());
        CsvHandler.deleteFile("centroids_history.csv", getApplicationContext());

        myToast.setText("Model has been reset");
        myToast.show();
        return true;    // true means that the long click is "consumed"
    }
}