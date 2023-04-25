package com.example.p6.classes;

import androidx.annotation.NonNull;

import com.example.p6.handlers.EllipseHandler;

import java.util.Locale;

public class Centroid {
    public double heartRate;
    public double minHeartRate;
    public double maxHeartRate;
    public double stepCount;
    public double minStepCount;
    public double maxStepCount;
    byte label;
    public int size;
    // Constructor where ellipse axes are calculated based on edge cases
    public Centroid(double heartRate, double minHeartRate, double maxHeartRate,
                    double stepCount, double minStepCount, double maxStepCount,
                    byte label, int size) {
        this.heartRate = heartRate;
        this.minHeartRate = minHeartRate;
        this.maxHeartRate = maxHeartRate;
        this.stepCount = stepCount;
        this.minStepCount = minStepCount;
        this.maxStepCount = maxStepCount;
        this.label = label;
        this.size = size;
    }

    // Constructor used when reading centroids from file
    public Centroid(String heartRate, String minHeartRate, String maxHeartRate,
                    String stepCount, String minStepCount, String maxStepCount,
                    String label, String size) {
        this.heartRate = Double.parseDouble(heartRate);
        this.minHeartRate = Double.parseDouble(minHeartRate);
        this.maxHeartRate = Double.parseDouble(maxHeartRate);
        this.stepCount = Double.parseDouble(stepCount);
        this.minStepCount = Double.parseDouble(minStepCount);
        this.maxStepCount = Double.parseDouble(maxStepCount);
        this.label = Byte.parseByte(label);
        this.size = Integer.parseInt(size);
    }

    @NonNull
    @Override
    public String toString(){
        return String.format(Locale.US, "%f,%f,%f,%f,%d",
                heartRate,
                stepCount,
                EllipseHandler.getSemiMajorAxis(this),
                EllipseHandler.getSemiMinorAxis(this),
                size
        );
    }

    public String toUIString(){
        return String.format(Locale.US, "%.2f, %.2f, %.2f, %.2f",
                heartRate, stepCount,
                EllipseHandler.getSemiMajorAxis(this),
                EllipseHandler.getSemiMinorAxis(this));
    }
}
