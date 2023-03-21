package com.example.p6;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.example.p6.databinding.ActivitySelectBinding;


public class SelectActivity extends Activity {

    enum Activity {
        IDLE,
        WALKING,
        RUNNING,
        CYCLING
    }

    private SelectActivity.Activity activityToTrack = SelectActivity.Activity.WALKING;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(!isTaskRoot()){  // If DisplayActivity is already running, redirect to that
            startDisplayActivity();
        }
        else {
            super.onCreate(savedInstanceState);
            ActivitySelectBinding binding = ActivitySelectBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
        }
    }

    public void startDisplayActivity(){
        Intent intent = new Intent(SelectActivity.this, DisplayActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("activityToTrack", activityToTrack.ordinal());
        startActivity(intent);
        finish();
    }

    public void onStartButtonClick(View view){
        startDisplayActivity();
    }

    public void onRadioButtonIdle(View view) {
        activityToTrack = SelectActivity.Activity.IDLE;
    }
    public void onRadioButtonWalking(View view) {
        activityToTrack = SelectActivity.Activity.WALKING;
    }
    public void onRadioButtonRunning(View view) {
        activityToTrack = SelectActivity.Activity.RUNNING;
    }
    public void onRadioButtonCycling(View view) {
        activityToTrack = SelectActivity.Activity.CYCLING;
    }

    public void onExitButtonClick(View view) {
        finishAndRemoveTask();
        System.exit(0);
    }
}