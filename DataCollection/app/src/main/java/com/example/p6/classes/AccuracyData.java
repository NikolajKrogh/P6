package com.example.p6.classes;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Locale;

public class AccuracyData {
    public double accuracy;
    public short correctPredictions;
    public short totalPredictions;
    public short sittingPredictions;
    public short walkingPredictions;
    public short runningPredictions;
    public short cyclingPredictions;

    public AccuracyData(
            double accuracy,
            short correctPredictions,
            short totalPredictions,
            short sittingPredictions,
            short walkingPredictions,
            short runningPredictions,
            short cyclingPredictions
            ){
        this.accuracy = accuracy;
        this.correctPredictions = correctPredictions;
        this.totalPredictions = totalPredictions;
        this.sittingPredictions = sittingPredictions;
        this.walkingPredictions = walkingPredictions;
        this.runningPredictions = runningPredictions;
        this.cyclingPredictions = cyclingPredictions;
    }

    public AccuracyData(List<Constants.Activity> predictedActivities, Constants.Activity activityToTrack){
        this.totalPredictions = (short) predictedActivities.size();

        if (this.totalPredictions == 0){
            this.accuracy = 1;
            this.correctPredictions = 0;
            this.sittingPredictions = 0;
            this.walkingPredictions = 0;
            this.runningPredictions = 0;
            this.cyclingPredictions = 0;
            return;
        }

        for (Constants.Activity activity : predictedActivities) {
            if (activity == activityToTrack){
                this.correctPredictions++;
            }
        }

        // get number of correct predictions for each activity
        for (Constants.Activity activity : predictedActivities) {
            switch (activity) {
                case SITTING:
                    this.sittingPredictions++;
                    break;
                case WALKING:
                    this.walkingPredictions++;
                    break;
                case RUNNING:
                    this.runningPredictions++;
                    break;
                case CYCLING:
                    this.cyclingPredictions++;
                    break;
                default:
                    throw new RuntimeException("Activity " + activity + " not recognized");
            }
        }

        this.accuracy = (double) this.correctPredictions / (double) this.totalPredictions;
    }

    @NonNull
    @Override
    public String toString(){
            return String.format(
                    Locale.US,
                    "%.4f,%d,%d,%d,%d,%d,%d",
                    accuracy,
                    correctPredictions,
                    totalPredictions,
                    sittingPredictions,
                    walkingPredictions,
                    runningPredictions,
                    cyclingPredictions
            );
    }
}
