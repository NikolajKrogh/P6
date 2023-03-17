package com.example.p6;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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
        super.onCreate(savedInstanceState);
    }

    public void onStartButtonClick(View view){
        // Launch new activity and clear old from history
        Intent intent = new Intent(SelectActivity.this, DisplayActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Send activityToTrack to next activity
        intent.putExtra("activityToTrack", activityToTrack.ordinal());

        startActivity(intent);
        finish();
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
        finish();
        System.exit(0);
    }
}