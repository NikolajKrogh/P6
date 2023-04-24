package com.example.p6.classes;

import androidx.annotation.NonNull;

import java.util.Locale;

public class DataPointAggregated {

    double heartRate;
    double stepCount;
    CentroidEdgeCases edgeCases;

    public DataPointAggregated(double heartRate, double stepCount, CentroidEdgeCases edgeCases) {
        this.heartRate = heartRate;
        this.stepCount = stepCount;
        this.edgeCases = edgeCases;
    }

    @NonNull
    @Override
    public String toString(){
        return String.format(Locale.US, "%f,%f\n", heartRate, stepCount);
    }
}
