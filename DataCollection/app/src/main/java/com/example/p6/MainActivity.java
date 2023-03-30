package com.example.p6;

import static com.example.p6.MainActivity.Screen.*;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.p6.databinding.ActivityMainBinding;
import com.example.p6.databinding.ActivitySelectBinding;

public class MainActivity extends Activity {
    enum Mode {
        RUN_MODEL,
        SYNCHRONIZE,
        COLLECT_DATA,
    }

    enum Screen {
        MAIN,
        SELECT,
        DISPLAY,
        VIEW_MODEL
    }

    static Screen currentScreen = MAIN;
    static boolean BackButtonPressed = false;

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
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        startActivity(intent);
    }

    public void startViewModelActivity(){
        Intent intent = new Intent(MainActivity.this, ViewModelActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT );
        startActivity(intent);
    }

    public void onRunModelClick(View view){
        Intent intent = new Intent(MainActivity.this, DisplayActivity.class);
        intent.putExtra("mode", Mode.RUN_MODEL.ordinal());
        startActivity(intent);
    }

    public void onSynchronizeModelClick(View view){
        Intent intent = new Intent(MainActivity.this, SelectActivity.class);
        intent.putExtra("mode", Mode.SYNCHRONIZE.ordinal());
        startActivity(intent);
    }

    public void onViewModelClick(View view){
        Intent intent = new Intent(MainActivity.this, ViewModelActivity.class);
        startActivity(intent);
    }

    public void onCollectDataClick(View view){
        Intent intent = new Intent(MainActivity.this, SelectActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.putExtra("mode", Mode.COLLECT_DATA.ordinal());
        startActivity(intent);
    }

    public void onExitButtonClick(View view) {
        finishAndRemoveTask();
        System.exit(0);
    }


}