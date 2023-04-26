package com.example.p6.classes;

public class Ellipse {
    public double heartRate;
    public double minHeartRate;
    public double maxHeartRate;
    public double stepCount;
    public double minStepCount;
    public double maxStepCount;

    public Ellipse(double heartRate, double minHeartRate, double maxHeartRate,
                   double stepCount, double minStepCount, double maxStepCount){
        this.heartRate = heartRate;
        this.minHeartRate = minHeartRate;
        this.maxHeartRate = maxHeartRate;
        this.stepCount = stepCount;
        this.minStepCount = minStepCount;
        this.maxStepCount = maxStepCount;
    }

    public double getSemiMajorAxis() {
        double distanceToMinHeartRate = getDifferenceBetweenTwoNumbers(minHeartRate, heartRate);
        double distanceToMaxHeartRate = getDifferenceBetweenTwoNumbers(maxHeartRate, heartRate);
        return Math.max(distanceToMinHeartRate, distanceToMaxHeartRate);
    }

    public double getSemiMinorAxis() {
        double distanceToMinStepCount = getDifferenceBetweenTwoNumbers(minStepCount, stepCount);
        double distanceToMaxStepCount = getDifferenceBetweenTwoNumbers(maxStepCount, stepCount);
        return Math.max(distanceToMinStepCount, distanceToMaxStepCount);
    }

    // Heavily inspired by:
    // https://www.geeksforgeeks.org/check-if-a-point-is-inside-outside-or-on-the-ellipse/
    public boolean contains(DataPointAggregated point){
        double p = (Math.pow((point.heartRate - heartRate), 2)
                / Math.pow(getBufferValue(getSemiMajorAxis()), 2))
                + (Math.pow((point.stepCount - stepCount), 2)
                / Math.pow(getBufferValue(getSemiMinorAxis()), 2));

        return p <= 1;
    }

    private double getDifferenceBetweenTwoNumbers(double x, double y) {
        return Math.abs(x - y);
    }

    private double getBufferValue(double axis){
        byte BUFFER_PERCENTAGE = 10;
        double factor = 1 + (double) BUFFER_PERCENTAGE / 100;
        return axis * factor;
    }

}
