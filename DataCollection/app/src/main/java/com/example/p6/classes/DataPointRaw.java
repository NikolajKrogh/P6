package com.example.p6.classes;

import androidx.annotation.NonNull;

import java.util.Locale;

public class DataPointRaw{
    public short heartRate;
    public int stepCount;
    byte label;
    public short minutes;

    public DataPointRaw(short heartRate, int stepCount, byte label, short minutes) {
        this.heartRate = heartRate;
        this.stepCount = stepCount;
        this.label = label;
        this.minutes = minutes;
    }

    @NonNull
    @Override
    public String toString(){
        return String.format(Locale.US, "%d,%d,%d,%d\n", minutes, heartRate, stepCount, label);
    }
}
