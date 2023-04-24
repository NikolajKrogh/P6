package com.example.p6.classes;

import androidx.annotation.NonNull;
import java.util.Locale;

public class Centroid {
    double heartRate;
    double stepCount;
    byte label;
    int size;
    double semiMajorAxis;
    double semiMinorAxis;

    //for before preprocessing
    public Centroid(double heartRate, double stepCount, byte label, int size) {
        this.heartRate = heartRate;
        this.stepCount = stepCount;
        this.semiMajorAxis = EllipseHandler.getSemiMajorAxis(this);
        this.semiMinorAxis = EllipseHandler.getSemiMinorAxis(this);
        this.label = label;
        this.size = size;
    }

    public Centroid(double heartRate, double stepCount, double semiMajorAxis, double semiMinorAxis,
                    byte label, int size) {
        this.heartRate = heartRate;
        this.stepCount = stepCount;
        this.semiMajorAxis = semiMajorAxis;
        this.semiMinorAxis = semiMinorAxis;
        this.label = label;
        this.size = size;
    }

    public Centroid(String heartRate, String stepCount,
                    String semiMajorAxis, String semiMinorAxis,
                    String label, String size) {
        this(
                Double.parseDouble(heartRate),
                Double.parseDouble(stepCount),
                Double.parseDouble(semiMajorAxis),
                Double.parseDouble(semiMinorAxis),
                Byte.parseByte(label),
                Integer.parseInt(size));
    }

    @NonNull
    @Override
    public String toString(){
        return String.format(Locale.US, "%f,%f,%f,%f,%d,%d",
                heartRate,
                stepCount,
                semiMajorAxis,
                semiMinorAxis,
                label,
                size
        );
    }

    public String toUIString(){
        return String.format(Locale.US, "%.2f, %.2f, %.2f, %.2f",
                heartRate, stepCount, semiMajorAxis, semiMinorAxis);
    }
}
