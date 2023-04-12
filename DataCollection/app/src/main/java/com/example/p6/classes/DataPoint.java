package com.example.p6.classes;

import android.util.Log;

import androidx.annotation.NonNull;

public class DataPoint {
    short heartRate;
    int stepCount;
    byte label;
    short minutes = -1;
    String sessionId;

    //for before preprocessing
    public DataPoint(short heartRate, int stepCount, byte label, short minutes) {
        this.heartRate = heartRate;
        this.stepCount = stepCount;
        this.label = label;
        this.minutes = minutes;
    }

    //for after preprocessing
    public DataPoint(short heartRate, int stepCount, String sessionId) {
        this.heartRate = heartRate;
        this.stepCount = stepCount;
        this.sessionId = sessionId;
    }

    @NonNull
    @Override
    public String toString(){
        if (minutes == -1){
            return String.format("%s,%d,%d,%d\n", sessionId, heartRate, stepCount, label);
        }
        else {
            return String.format  ("%d,%d,%d,%d\n", minutes, heartRate, stepCount, label);
        }
    }
}
