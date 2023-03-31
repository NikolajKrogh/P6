package com.example.p6;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.p6.databinding.ActivityViewModelBinding;

public class ViewModelActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("View model", "Created");
        MainActivity.currentScreen = MainActivity.Screen.VIEW_MODEL;
        super.onCreate(savedInstanceState);
        ActivityViewModelBinding binding = ActivityViewModelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    public void onBackButtonClick(View view) {
        Intent intent = new Intent(ViewModelActivity.this, MainActivity.class);
        MainActivity.BackButtonPressed = true;
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        finish();
    }
}