package com.example.p6.classes;

import androidx.annotation.NonNull;

import java.util.Locale;

public class DataPointAggregated {
    public double heartRate;
    public double minHeartRate;
    public double maxHeartRate;
    public double stepCount;
    public double[] distanceToCentroids = {0, 0, 0 ,0};

    public DataPointAggregated(double heartRate, double minHeartRate, double maxHeartRate,
                               double stepCount) {
        this.heartRate = heartRate;
        this.minHeartRate = minHeartRate;
        this.maxHeartRate = maxHeartRate;
        this.stepCount = stepCount;
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() != DataPointAggregated.class) {
            return false;
        }

        return Double.compare(this.heartRate, ((DataPointAggregated) other).heartRate) == 0 &&
                Double.compare(this.minHeartRate, ((DataPointAggregated) other).minHeartRate) == 0 &&
                Double.compare(this.maxHeartRate, ((DataPointAggregated) other).maxHeartRate) == 0 &&
                Double.compare(this.stepCount, ((DataPointAggregated) other).stepCount) == 0 &&
                Double.compare(this.distanceToCentroids[Constants.Activity.SITTING.ordinal()],
                        ((DataPointAggregated) other).distanceToCentroids[Constants.Activity.SITTING.ordinal()]) == 0 &&
                Double.compare(this.distanceToCentroids[Constants.Activity.WALKING.ordinal()],
                        ((DataPointAggregated) other).distanceToCentroids[Constants.Activity.WALKING.ordinal()]) == 0 &&
                Double.compare(this.distanceToCentroids[Constants.Activity.RUNNING.ordinal()],
                        ((DataPointAggregated) other).distanceToCentroids[Constants.Activity.RUNNING.ordinal()]) == 0 &&
                Double.compare(this.distanceToCentroids[Constants.Activity.CYCLING.ordinal()],
                        ((DataPointAggregated) other).distanceToCentroids[Constants.Activity.CYCLING.ordinal()]) == 0;
    }

    @NonNull
    @Override
    public String toString(){
        return String.format(Locale.US, "%f,%f,%f,%f,%f,%f,%f,%f",
                this.heartRate, this.minHeartRate, this.maxHeartRate, this.stepCount,
                this.distanceToCentroids[Constants.Activity.SITTING.ordinal()],
                this.distanceToCentroids[Constants.Activity.WALKING.ordinal()],
                this.distanceToCentroids[Constants.Activity.RUNNING.ordinal()],
                this.distanceToCentroids[Constants.Activity.CYCLING.ordinal()]);
    }
}
