package com.example.p6;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.p6.databinding.ActivityMainBinding;
import com.example.p6.databinding.ActivitySelectBinding;

public class MainActivity extends Activity {

    private TextView mTextView;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySelectBinding binding = ActivitySelectBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    public void startCurrentlyRunningActivity(){
        Intent intent = new Intent(MainActivity.this, DisplayActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("activityToTrack", activityToTrack.ordinal());
        startActivity(intent);
        finish();
    }
}