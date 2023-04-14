package com.example.p6.classes;

import android.util.Log;

import androidx.annotation.NonNull;

public class DataPoint {
    short heartRate;
    int stepCount;
    byte label;
    short minutes = -1;

    //for before preprocessing
    public DataPoint(short heartRate, int stepCount, byte label, short minutes) {
        this.heartRate = heartRate;
        this.stepCount = stepCount;
        this.label = label;
        this.minutes = minutes;
    }

    //for after preprocessing
    public DataPoint(short heartRate, int stepCount) {
        this.heartRate = heartRate;
        this.stepCount = stepCount;
    }

    @NonNull
    @Override
    public String toString(){
        if (minutes == -1) { //if the datapoint has been processed
            return String.format("%d,%d\n", heartRate, stepCount);
        }
        else {
            return String.format  ("%d,%d,%d,%d\n", minutes, heartRate, stepCount, label);
        }
    }
}
