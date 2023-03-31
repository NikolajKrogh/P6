package com.example.p6;

import static com.example.p6.MainActivity.Activity.*;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.p6.databinding.ActivitySelectBinding;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;


public class SelectActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MainActivity.currentScreen = MainActivity.Screen.SELECT;
        super.onCreate(savedInstanceState);
        ActivitySelectBinding binding = ActivitySelectBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    public void onStartButtonClick(View view){
        startDisplayActivity();
    }

    public void startDisplayActivity(){
        Intent intent = new Intent(SelectActivity.this, DisplayActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        finish();
    }

    public void onRadioButtonSitting(View view) {
        MainActivity.activityToTrack = SITTING;
    }
    public void onRadioButtonWalking(View view) {
        MainActivity.activityToTrack = WALKING;
    }
    public void onRadioButtonRunning(View view) {
        MainActivity.activityToTrack = RUNNING;
    }
    public void onRadioButtonCycling(View view) {
        MainActivity.activityToTrack = CYCLING;
    }

    public void onBackButtonClick(View view) {
        Intent intent = new Intent(SelectActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        MainActivity.BackButtonPressed = true;
        startActivity(intent);
        finish();
    }
}