package com.example.p6.classes;

import androidx.annotation.NonNull;

public class Centroid {
    double heartRate;
    double step_count;
    byte label;
    int size;

    //for before preprocessing
    public Centroid(double heart_rate, double step_count, byte label, int size) {
        this.heartRate = heart_rate;
        this.step_count = step_count;
        this.label = label;
        this.size = size;
    }

    public Centroid(String heart_rate, String step_count, String label, String size) {
        this(Double.parseDouble(heart_rate),Double.parseDouble(step_count),Byte.parseByte(label), Integer.parseInt(size));
    }

    @NonNull
    @Override
    public String toString(){
        return String.format("%f,%f,%d,%d\n", heartRate, step_count, label, size);
    }
}
