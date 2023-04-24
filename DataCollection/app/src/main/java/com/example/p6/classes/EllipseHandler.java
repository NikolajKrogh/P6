package com.example.p6.classes;

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
        double distanceToEasternMostPoint = getDistanceToCentroid(centroid.edgeCases.easternMostPoint, centroid);
        double distanceToWesternMostPoint = getDistanceToCentroid(centroid.edgeCases.westernMostPoint, centroid);
        return Math.max(distanceToEasternMostPoint, distanceToWesternMostPoint);
    }

    public static double getSemiMinorAxis(Centroid centroid) {
        double distanceToNorthernMostPoint = getDistanceToCentroid(centroid.edgeCases.northernMostPoint, centroid);
        double distanceToSouthernMostPoint = getDistanceToCentroid(centroid.edgeCases.southernMostPoint, centroid);
        return Math.max(distanceToNorthernMostPoint, distanceToSouthernMostPoint);
    }

    private static double getDistanceToCentroid(DataPointBasic dataPoint, Centroid centroid) {
        double x1 = centroid.heartRate;
        double y1 = centroid.stepCount;
        double x2 = dataPoint.heartRate;
        double y2 = dataPoint.stepCount;

        return Math.sqrt(Math.pow((y2 - y1), 2) + Math.pow((x2 - x1), 2));
    }
}
