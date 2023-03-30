package com.example.p6;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import com.example.p6.databinding.ActivitySelectBinding;


public class SelectActivity extends Activity {

    enum Activity {
        SITTING,
        WALKING,
        RUNNING,
        CYCLING
    }

    private SelectActivity.Activity activityToTrack = SelectActivity.Activity.WALKING;
    private MainActivity.Mode mode = MainActivity.Mode.COLLECT_DATA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MainActivity.currentScreen = MainActivity.Screen.SELECT;
        super.onCreate(savedInstanceState);
        ActivitySelectBinding binding = ActivitySelectBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        retrieveDataFromPreviousActivity();
    }

    public void retrieveDataFromPreviousActivity(){
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras != null){
            int activityOrdinal = extras.getInt("activityToTrack");
            int modeOrdinal = extras.getInt("mode");
            activityToTrack = SelectActivity.Activity.values()[activityOrdinal];
            mode = MainActivity.Mode.values()[modeOrdinal];
        }
    }

    public void onStartButtonClick(View view){
        startDisplayActivity();
    }

    public void startDisplayActivity(){
        Intent intent = new Intent(SelectActivity.this, DisplayActivity.class);
        intent.putExtra("activityToTrack", activityToTrack.ordinal());
        intent.putExtra("mode", mode.ordinal());
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        startActivity(intent);
        finish();
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

    public void onBackButtonClick(View view) {
        Intent intent = new Intent(SelectActivity.this, MainActivity.class);
        MainActivity.BackButtonPressed = true;
        startActivity(intent);
        finish();
    }
}