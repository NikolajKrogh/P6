package com.example.p6.activities;

import static com.example.p6.classes.Constants.Activity.*;
import static com.example.p6.classes.Constants.Screen.*;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.p6.databinding.ActivitySelectBinding;


public class SelectActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MainActivity.currentScreen = SELECT;
        MainActivity.activityToTrack = WALKING;
        super.onCreate(savedInstanceState);
        ActivitySelectBinding binding = ActivitySelectBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    public void onStartButtonClick(View view){
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