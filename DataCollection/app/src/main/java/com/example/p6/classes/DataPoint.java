package com.example.p6.classes;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Locale;

public class DataPoint {
    public short heartRate;
    public int stepCount;
    public byte label;
    public short minutes = -1;
    public String sessionId;

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
            return String.format(Locale.US, "%s,%d,%d,%d\n", sessionId, heartRate, stepCount, label);
        }
        else {
            return String.format(Locale.US, "%d,%d,%d,%d\n", minutes, heartRate, stepCount, label);
        }
    }
}
