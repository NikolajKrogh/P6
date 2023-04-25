package com.example.p6.handlers;

import com.example.p6.classes.Centroid;
import com.example.p6.classes.DataPointAggregated;

public class EllipseHandler {

    private static final byte BUFFER_PERCENTAGE = 10;

    // Heavily inspired by:
    // https://www.geeksforgeeks.org/check-if-a-point-is-inside-outside-or-on-the-ellipse/
    public static boolean checkIfDataPointWithinEllipse(DataPointAggregated point, Centroid centroid){
        double p = (Math.pow((point.heartRate - centroid.heartRate), 2)
                / Math.pow(getBufferValue(EllipseHandler.getSemiMajorAxis(centroid)), 2))
                + (Math.pow((point.stepCount - centroid.stepCount), 2)
                / Math.pow(getBufferValue(EllipseHandler.getSemiMinorAxis(centroid)), 2));

        return p <= 1;
    }

    private static double getBufferValue(double axis){
        double factor = 1 + (double) BUFFER_PERCENTAGE / 100;
        return axis * factor;
    }

    public static double getSemiMajorAxis(Centroid centroid) {
        double distanceToMinHeartRate = getDifferenceBetweenTwoNumbers(centroid.minHeartRate, centroid.heartRate);
        double distanceToMaxHeartRate = getDifferenceBetweenTwoNumbers(centroid.maxHeartRate, centroid.heartRate);
        return Math.max(distanceToMinHeartRate, distanceToMaxHeartRate);
    }

    public static double getSemiMinorAxis(Centroid centroid) {
        double distanceToMinStepCount = getDifferenceBetweenTwoNumbers(centroid.minStepCount, centroid.stepCount);
        double distanceToMaxStepCount = getDifferenceBetweenTwoNumbers(centroid.maxStepCount, centroid.stepCount);
        return Math.max(distanceToMinStepCount, distanceToMaxStepCount);
    }

    private static double getDifferenceBetweenTwoNumbers(double x, double y) {
        return Math.abs(x - y);
    }
}
