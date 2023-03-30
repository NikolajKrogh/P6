package com.example.p6;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.p6.databinding.ActivitySelectBinding;


public class SelectActivity extends Activity {

    enum Activity {
        SITTING,
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
    public static int getBatteryPercentage(Context context) {
            BatteryManager bm = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
            return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
    }

    public void startDisplayActivity(){
        Intent intent = new Intent(SelectActivity.this, DisplayActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("activityToTrack", activityToTrack.ordinal());
        startActivity(intent);

        Log.i("BatteryLevelStart",Integer.toString(getBatteryPercentage(getApplicationContext())));
        finish();
    }

    public void onStartButtonClick(View view){
        startDisplayActivity();
    }

    public void onRadioButtonSitting(View view) {
        activityToTrack = SelectActivity.Activity.SITTING;
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