package com.example.p6.classes;

import androidx.annotation.NonNull;

import java.util.Locale;

public class DataPointAggregated extends DataPointBasic {
    CentroidEdgeCases edgeCases;

    public DataPointAggregated(double heartRate, double stepCount, CentroidEdgeCases edgeCases) {
        super(heartRate, stepCount);
        this.edgeCases = edgeCases;
    }
}
