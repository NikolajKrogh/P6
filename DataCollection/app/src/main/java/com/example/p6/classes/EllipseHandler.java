package com.example.p6.classes;

public class EllipseHandler {

    private static final byte BUFFER_PERCENTAGE = 10;

    // Heavily inspired by:
    // https://www.geeksforgeeks.org/check-if-a-point-is-inside-outside-or-on-the-ellipse/
    public boolean checkIfPointWithinEllipse(DataPointAggregated point, Centroid centroid){
        double p = (Math.pow((point.heartRate - centroid.heartRate), 2)
                / Math.pow(getBufferValue(centroid.semiMajorAxis), 2))
                + (Math.pow((point.stepCount - centroid.stepCount), 2)
                / Math.pow(getBufferValue(centroid.semiMinorAxis), 2));

        return p <= 1;
    }

    private double getBufferValue(double axis){
        double factor = 1 + (double) BUFFER_PERCENTAGE / 100;
        return axis * factor;
    }
}
