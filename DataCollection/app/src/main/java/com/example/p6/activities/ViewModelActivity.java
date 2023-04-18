package com.example.p6.activities;

import static com.example.p6.classes.Constants.Screen.*;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.p6.classes.Centroid;
import com.example.p6.classes.Constants;
import com.example.p6.classes.CsvHandler;
import com.example.p6.databinding.ActivityViewModelBinding;
import com.example.p6.classes.NearestCentroid;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;

public class ViewModelActivity extends Activity {

    private TextView originalSittingCentroidText;
    private TextView originalWalkingCentroidText;
    private TextView originalRunningCentroidText;
    private TextView originalCyclingCentroidText;
    private TextView currentSittingCentroidText;
    private TextView currentWalkingCentroidText;
    private TextView currentRunningCentroidText;
    private TextView currentCyclingCentroidText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MainActivity.currentScreen = VIEW_MODEL;
        super.onCreate(savedInstanceState);
        ActivityViewModelBinding binding = ActivityViewModelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        bindTextToVariables();

        try {
            getAndSetCentroidValues();
        } catch (CsvValidationException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void onBackButtonClick(View view) {
        Intent intent = new Intent(ViewModelActivity.this, MainActivity.class);
        MainActivity.BackButtonPressed = true;
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        finish();
    }

    private void bindTextToVariables(){
        ActivityViewModelBinding binding = ActivityViewModelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        originalSittingCentroidText = binding.originalSittingCentroidText;
        originalWalkingCentroidText = binding.originalWalkingCentroidText;
        originalRunningCentroidText = binding.originalRunningCentroidText;
        originalCyclingCentroidText = binding.originalCyclingCentroidText;

        currentSittingCentroidText = binding.currentSittingCentroidText;
        currentWalkingCentroidText = binding.currentWalkingCentroidText;
        currentRunningCentroidText = binding.currentRunningCentroidText;
        currentCyclingCentroidText = binding.currentCyclingCentroidText;
    }

    private void getAndSetCentroidValues() throws CsvValidationException, IOException {
        Centroid[] generalModelCentroids = NearestCentroid.generalModelCentroids;
        Centroid[] currentModelCentroids = CsvHandler.getCentroidsFromFile(getApplicationContext());

        originalSittingCentroidText.setText(generalModelCentroids[Constants.Activity.SITTING.ordinal()].toUIString());
        originalWalkingCentroidText.setText(generalModelCentroids[Constants.Activity.WALKING.ordinal()].toUIString());
        originalRunningCentroidText.setText(generalModelCentroids[Constants.Activity.RUNNING.ordinal()].toUIString());
        originalCyclingCentroidText.setText(generalModelCentroids[Constants.Activity.CYCLING.ordinal()].toUIString());

        currentSittingCentroidText.setText(currentModelCentroids[Constants.Activity.SITTING.ordinal()].toUIString());
        currentWalkingCentroidText.setText(currentModelCentroids[Constants.Activity.WALKING.ordinal()].toUIString());
        currentRunningCentroidText.setText(currentModelCentroids[Constants.Activity.RUNNING.ordinal()].toUIString());
        currentCyclingCentroidText.setText(currentModelCentroids[Constants.Activity.CYCLING.ordinal()].toUIString());
    }

}