package com.example.p6;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.p6.databinding.ActivitySelectBinding;

import java.time.LocalDateTime;

public class SelectActivity extends Activity {

    enum Activity {
        IDLE,
        WALKING,
        RUNNING,
        CYCLING
    }

    private SelectActivity.Activity activityToTrack = SelectActivity.Activity.WALKING;

    private TextView mTextView;
    private ActivitySelectBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        binding = ActivitySelectBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    public void onStartButtonClick(View view){
        Intent intent = new Intent(SelectActivity.this, DisplayActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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