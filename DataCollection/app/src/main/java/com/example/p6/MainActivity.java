package com.example.p6;

import static com.example.p6.MainActivity.Activity.*;
import static com.example.p6.MainActivity.Screen.*;
import static com.example.p6.MainActivity.Mode.*;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.p6.databinding.ActivityMainBinding;

public class MainActivity extends Activity {
    enum Activity {
        SITTING,
        WALKING,
        RUNNING,
        CYCLING
    }

    enum Screen {
        MAIN,
        SELECT,
        DISPLAY,
        VIEW_MODEL
    }

    enum Mode {
        RUN_MODEL,
        SYNCHRONIZE,
        COLLECT_DATA,
    }

    static Activity activityToTrack = WALKING;
    static Screen currentScreen = MAIN;
    static Mode trackingMode = COLLECT_DATA;
    static boolean BackButtonPressed = false;
    static boolean HomeButtonPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    public void onResume(){
        super.onResume();
        if (!BackButtonPressed){   // If we came from the home menu of the watch
            startCurrentlyRunningActivity();
        }
        BackButtonPressed = false;
    }

    public void startCurrentlyRunningActivity(){
        switch (currentScreen){
            case SELECT:        startSelectActivity();      break;
            case DISPLAY:       startDisplayActivity();     break;
            case VIEW_MODEL:    startViewModelActivity();   break;
        }
    }

    public void startSelectActivity(){
        Intent intent = new Intent(MainActivity.this, SelectActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    public void startDisplayActivity(){
        Intent intent = new Intent(MainActivity.this, DisplayActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    public void startViewModelActivity(){
        Intent intent = new Intent(MainActivity.this, ViewModelActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    public void onRunModelClick(View view){
        Intent intent = new Intent(MainActivity.this, DisplayActivity.class);
        trackingMode = RUN_MODEL;
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    public void onSynchronizeModelClick(View view){
        Intent intent = new Intent(MainActivity.this, SelectActivity.class);
        trackingMode = SYNCHRONIZE;
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    public void onViewModelClick(View view){
        Intent intent = new Intent(MainActivity.this, ViewModelActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    public void onCollectDataClick(View view){
        Intent intent = new Intent(MainActivity.this, SelectActivity.class);
        trackingMode = COLLECT_DATA;
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    public void onExitButtonClick(View view) {
        finishAndRemoveTask();
        System.exit(0);
    }


}