package com.example.p6.classes;

import androidx.annotation.NonNull;

import java.util.Locale;

public class DataPointAggregated {

    double heartRate;
    double stepCount;

    public DataPointAggregated(double heartRate, double stepCount) {
        this.heartRate = heartRate;
        this.stepCount = stepCount;
    }

    @NonNull
    @Override
    public String toString(){
        return String.format(Locale.US, "%f,%f\n", heartRate, stepCount);
    }
}
