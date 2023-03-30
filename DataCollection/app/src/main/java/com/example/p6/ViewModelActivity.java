package com.example.p6;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.p6.databinding.ActivityViewModelBinding;

public class ViewModelActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MainActivity.currentScreen = MainActivity.Screen.VIEW_MODEL;
        super.onCreate(savedInstanceState);
        ActivityViewModelBinding binding = ActivityViewModelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    public void onBackButtonClick(View view) {
        Intent intent = new Intent(ViewModelActivity.this, MainActivity.class);
        MainActivity.BackButtonPressed = true;
        startActivity(intent);
        finish();
    }
}