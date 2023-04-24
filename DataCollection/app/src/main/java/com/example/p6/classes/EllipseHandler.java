package com.example.p6.classes;

import android.util.Log;

public class EllipseHandler {

    private static final byte BUFFER_PERCENTAGE = 10;

    // Heavily inspired by:
    // https://www.geeksforgeeks.org/check-if-a-point-is-inside-outside-or-on-the-ellipse/
    public static boolean checkIfPointWithinEllipse(DataPointAggregated point, Centroid centroid){
        double p = (Math.pow((point.heartRate - centroid.heartRate), 2)
                / Math.pow(getBufferValue(centroid.semiMajorAxis), 2))
                + (Math.pow((point.stepCount - centroid.stepCount), 2)
                / Math.pow(getBufferValue(centroid.semiMinorAxis), 2));

        return p <= 1;
    }

    private static double getBufferValue(double axis){
        double factor = 1 + (double) BUFFER_PERCENTAGE / 100;
        return axis * factor;
    }

    public static double getSemiMajorAxis(Centroid centroid) {
        double distanceToEasternMostPoint = getDifferenceBetweenTwoNumbers(centroid.edgeCases.easternMostPoint.stepCount, centroid.stepCount);
        double distanceToWesternMostPoint = getDifferenceBetweenTwoNumbers(centroid.edgeCases.westernMostPoint.stepCount, centroid.stepCount);
        return Math.max(distanceToEasternMostPoint, distanceToWesternMostPoint);
    }

    public static double getSemiMinorAxis(Centroid centroid) {
        double distanceToNorthernMostPoint = getDifferenceBetweenTwoNumbers(centroid.edgeCases.northernMostPoint.heartRate, centroid.heartRate);
        double distanceToSouthernMostPoint = getDifferenceBetweenTwoNumbers(centroid.edgeCases.southernMostPoint.heartRate, centroid.heartRate);
        return Math.max(distanceToNorthernMostPoint, distanceToSouthernMostPoint);
    }

    private static double getDifferenceBetweenTwoNumbers(double x, double y) {
        return Math.abs(x - y);
    }
}
