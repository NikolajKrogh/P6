package com.example.p6.classes;

import static com.example.p6.classes.Constants.Activity.*;

import androidx.annotation.NonNull;

import com.example.p6.classes.Constants.Activity;

import java.util.Locale;

public class Centroid {
    public double heartRate;
    public double stepCount;
    public Ellipse ellipse;
    byte label;
    public int size;

    // Constructor where ellipse axes are calculated based on edge cases
    public Centroid(double heartRate, double minHeartRate, double maxHeartRate,
                    double stepCount, double minStepCount, double maxStepCount,
                    byte label, int size) {
        this.heartRate = heartRate;
        this.stepCount = stepCount;
        this.label = label;
        this.size = size;
        setEllipse(minHeartRate, maxHeartRate, minStepCount, maxStepCount);
    }

    // Constructor used when reading centroids from file
    public Centroid(String heartRate, String minHeartRate, String maxHeartRate,
                    String stepCount, String minStepCount, String maxStepCount,
                    String label, String size) {
        this.heartRate = Double.parseDouble(heartRate);
        this.stepCount = Double.parseDouble(stepCount);
        this.label = Byte.parseByte(label);
        this.size = Integer.parseInt(size);
        setEllipse(Double.parseDouble(minHeartRate),
                Double.parseDouble(maxHeartRate),
                Double.parseDouble(minStepCount),
                Double.parseDouble(maxStepCount));
    }

    public void setEllipse(double minHeartRate, double maxHeartRate, double minStepCount, double maxStepCount){
        Activity activity = Activity.values()[this.label];

        double ellipseHeartRate = (minHeartRate + maxHeartRate) / 2;
        double ellipseStepCount = (minStepCount + maxStepCount) / 2;

        if (activity == SITTING || activity == CYCLING){
            ellipseStepCount = this.stepCount;
        }

        this.ellipse = new Ellipse(ellipseHeartRate, minHeartRate, maxHeartRate,
                ellipseStepCount, minStepCount, maxStepCount);
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() != Centroid.class) {
            return false;
        }

        return Double.compare(this.heartRate, ((Centroid) other).heartRate) <= Constants.DELTA &&
                Double.compare(this.ellipse.minHeartRate, ((Centroid) other).ellipse.minHeartRate) <= Constants.DELTA &&
                Double.compare(this.ellipse.maxHeartRate, ((Centroid) other).ellipse.maxHeartRate) <= Constants.DELTA &&
                Double.compare(this.stepCount, ((Centroid) other).stepCount) <= Constants.DELTA &&
                Double.compare(this.ellipse.heartRate, ((Centroid) other).ellipse.heartRate) <= Constants.DELTA &&
                Double.compare(this.ellipse.minHeartRate, ((Centroid) other).ellipse.minHeartRate) <= Constants.DELTA &&
                Double.compare(this.ellipse.maxHeartRate, ((Centroid) other).ellipse.maxHeartRate) <= Constants.DELTA &&
                Double.compare(this.ellipse.stepCount, ((Centroid) other).ellipse.stepCount) <= Constants.DELTA &&
                Double.compare(this.ellipse.minStepCount, ((Centroid) other).ellipse.minStepCount) <= Constants.DELTA &&
                Double.compare(this.ellipse.getMaxStepCount(), ((Centroid) other).ellipse.getMaxStepCount()) <= Constants.DELTA &&
                this.label == ((Centroid)other).label &&
                this.size == ((Centroid)other).size;
    }

    @NonNull
    @Override
    public String toString(){
        return String.format(Locale.US, "%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%d,%d",
                heartRate,
                ellipse.minHeartRate,
                ellipse.maxHeartRate,
                stepCount,
                ellipse.minStepCount,
                ellipse.heartRate,
                ellipse.stepCount,
                ellipse.getMaxStepCount(),
                ellipse.getSemiMajorAxis(),
                ellipse.getSemiMinorAxis(),
                label,
                size
        );
    }

    public String formatUIString(){
        return String.format(Locale.US, Constants.Activity.values()[label].name() + ":\n" +
                "Centroid: %.2f, %.2f\nEllipse-center: %.2f, %.2f\nEllipse-axes: %.2f, %.2f\n",
                heartRate, stepCount, ellipse.heartRate, ellipse.stepCount,
                ellipse.getSemiMajorAxis(),
                ellipse.getSemiMinorAxis());
    }
}
