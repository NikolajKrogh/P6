package com.example.p6;

import static com.example.p6.MainActivity.Activity.*;
import static com.example.p6.MainActivity.Screen.*;
import static com.example.p6.MainActivity.Mode.*;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.p6.databinding.ActivityMainBinding;

public class MainActivity extends Activity implements View.OnLongClickListener, View.OnClickListener {

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
        myToast.setText("Model has been reset (but not really)");
        myToast.show();
        return true;
    }
}