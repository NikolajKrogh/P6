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
    public double sittingPredictionRate = 0;
    public double walkingPredictionRate = 0;
    public double runningPredictionRate = 0;
    public double cyclingPredictionRate = 0;
    public double unlabeledPredictionRate = 0;

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

        this.accuracy = getPredictionRate(this.correctPredictions);
        this.sittingPredictionRate = getPredictionRate(this.sittingPredictions);
        this.walkingPredictionRate = getPredictionRate(this.walkingPredictions);
        this.runningPredictionRate = getPredictionRate(this.runningPredictions);
        this.cyclingPredictionRate = getPredictionRate(this.cyclingPredictions);
        this.unlabeledPredictionRate = getPredictionRate(this.unlabeledPredictions);
    }

    public double getPredictionRate(int numerator) {
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
                    sittingPredictionRate,
                    walkingPredictionRate,
                    runningPredictionRate,
                    cyclingPredictionRate,
                    unlabeledPredictionRate
            );
    }
}
