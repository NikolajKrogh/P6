package com.example.p6.classes;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Locale;

public class AccuracyData {
    public double accuracy = 1;
    public short correctPredictions = 0;
    public short totalPredictions = 0;
    public short sittingPredictions = 0;
    public short walkingPredictions = 0;
    public short runningPredictions = 0;
    public short cyclingPredictions = 0;
    public short unlabeledPredictions = 0;
    public double sittingPercentage = 0;
    public double walkingPercentage = 0;
    public double runningPercentage = 0;
    public double cyclingPercentage = 0;
    public double unlabeledPercentage = 0;

    public AccuracyData() {

    }

    public AccuracyData(List<Constants.Activity> predictedActivities, Constants.Activity activityToTrack){
        this.totalPredictions = (short) predictedActivities.size();

        if (this.totalPredictions == 0) {
            return;
        }

        for (Constants.Activity activity : predictedActivities) {
            if (activity == activityToTrack) {
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
                case UNLABELED:
                    this.unlabeledPredictions++;
                    break;
                default:
                    throw new RuntimeException("Activity " + activity + " not recognized");
            }
        }

        this.accuracy = getPercentage(this.correctPredictions);
        this.sittingPercentage = getPercentage(this.sittingPredictions);
        this.walkingPercentage = getPercentage(this.walkingPredictions);
        this.runningPercentage = getPercentage(this.runningPredictions);
        this.cyclingPercentage = getPercentage(this.cyclingPredictions);
        this.unlabeledPercentage = getPercentage(this.unlabeledPredictions);
    }

    public double getPercentage(int numerator) {
        return (double) numerator / (double) this.totalPredictions;
    }

    @NonNull
    @Override
    public String toString() {
            return String.format(
                    Locale.US,
                    "%.4f,%d,%d,%d,%d,%d,%d,%d,%.4f,%.4f,%.4f,%.4f,%.4f",
                    accuracy,
                    correctPredictions,
                    totalPredictions,
                    sittingPredictions,
                    walkingPredictions,
                    runningPredictions,
                    cyclingPredictions,
                    unlabeledPredictions,
                    sittingPercentage,
                    walkingPercentage,
                    runningPercentage,
                    cyclingPercentage,
                    unlabeledPercentage
            );
    }
}
