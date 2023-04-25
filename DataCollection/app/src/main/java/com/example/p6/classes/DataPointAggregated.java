package com.example.p6.classes;

public class DataPointAggregated {
    public double heartRate;
    public double minHeartRate;
    public double maxHeartRate;
    public double stepCount;
    public double minStepCount;
    public double maxStepCount;
    public double[] distanceToCentroids = {0, 0, 0 ,0};

    public DataPointAggregated(double heartRate, double minHeartRate, double maxHeartRate,
                               double stepCount, double minStepCount, double maxStepCount) {
        this.heartRate = heartRate;
        this.minHeartRate = minHeartRate;
        this.maxHeartRate = maxHeartRate;
        this.stepCount = stepCount;
        this.minStepCount = minStepCount;
        this.maxStepCount = maxStepCount;
    }
}
