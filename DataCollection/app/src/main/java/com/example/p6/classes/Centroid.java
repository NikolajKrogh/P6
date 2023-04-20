package com.example.p6.classes;

import androidx.annotation.NonNull;

import java.util.Locale;

public class Centroid {
    double heartRate;
    double stepCount;
    CentroidEdgeCases edgeCases;
    byte label;
    int size;
    double semiMajorAxis = -1;
    double semiMinorAxis = -1;

    //for before preprocessing
    public Centroid(double heartRate, double stepCount,
                    CentroidEdgeCases edgeCases, byte label, int size) {
        this.heartRate = heartRate;
        this.stepCount = stepCount;
        this.edgeCases = edgeCases;
        this.label = label;
        this.size = size;
        this.semiMajorAxis = getSemiMajorAxis();
        this.semiMinorAxis = getSemiMinorAxis();
    }

    public Centroid(double heartRate, double stepCount,
                    CentroidEdgeCases edgeCases, byte label, int size,
                    double semiMajorAxis, double semiMinorAxis) {
        this.heartRate = heartRate;
        this.stepCount = stepCount;
        this.edgeCases = edgeCases;
        this.label = label;
        this.size = size;
        this.semiMajorAxis = semiMajorAxis;
        this.semiMinorAxis = semiMinorAxis;
    }

    private double getSemiMajorAxis() {
        double distanceToEasternMostPoint = getDistanceToCentroid(this.edgeCases.easternMostPoint);
        double distanceToWesternMostPoint = getDistanceToCentroid(this.edgeCases.westernMostPoint);
        return Math.max(distanceToEasternMostPoint, distanceToWesternMostPoint);
    }

    private double getSemiMinorAxis() {
        double distanceToNorthernMostPoint = getDistanceToCentroid(this.edgeCases.northernMostPoint);
        double distanceToSouthernMostPoint = getDistanceToCentroid(this.edgeCases.southernMostPoint);
        return Math.max(distanceToNorthernMostPoint, distanceToSouthernMostPoint);
    }

    private double getDistanceToCentroid(DataPointAggregated dataPoint) {
        double x1 = heartRate;
        double y1 = stepCount;
        double x2 = dataPoint.heartRate;
        double y2 = dataPoint.stepCount;

        return Math.sqrt(Math.pow((y2 - y1), 2) + Math.pow((x2 - x1), 2));
    }

    public Centroid(String heartRate, String stepCount,
                    String northerMostPointX, String northernMostPointY,
                    String easternMostPointX, String easternMostPointY,
                    String southernMostPointX,String southernMostPointY,
                    String westernMostPointX, String westernMostPointY,
                    String label, String size,
                    String semiMajorAxis, String semiMinorAxis) {
        this(
                Double.parseDouble(heartRate),
                Double.parseDouble(stepCount),
                new CentroidEdgeCases()
                Byte.parseByte(label),
                Integer.parseInt(size),
                Double.parseDouble(semiMajorAxis),
                Double.parseDouble(semiMinorAxis));
    }

    @NonNull
    @Override
    public String toString(){
        return String.format(Locale.US, "%f,%f,%d,%d", heartRate, stepCount, label, size);
    }

    public String toUIString(){
        return String.format(Locale.US, "%.2f, %.2f", heartRate, stepCount);
    }
}
