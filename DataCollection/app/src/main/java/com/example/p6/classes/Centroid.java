package com.example.p6.classes;

import androidx.annotation.NonNull;

public class Centroid {
    double heartRate;
    double stepCount;
    byte label;
    int size;

    //for before preprocessing
    public Centroid(double heartRate, double stepCount, byte label, int size) {
        this.heartRate = heartRate;
        this.stepCount = stepCount;
        this.label = label;
        this.size = size;
    }

    public Centroid(String heartRate, String stepCount, String label, String size) {
        this(Double.parseDouble(heartRate),Double.parseDouble(stepCount),Byte.parseByte(label), Integer.parseInt(size));
    }

    @NonNull
    @Override
    public String toString(){
        return String.format("%f,%f,%d,%d", heartRate, stepCount, label, size);
    }
}
