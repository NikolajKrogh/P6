package com.example.p6.classes;

import androidx.annotation.NonNull;

public class Row{
    short heartRate;
    int step_count;
    byte label;
    short minutes;
    String sessionId;
    boolean hasBeenPreprocessed;

    //for before preprocessing
    public Row(short heartRate, int stepCount, byte label, short minutes) {
        this.heartRate = heartRate;
        this.step_count = stepCount;
        this.label = label;
        this.minutes = minutes;
        hasBeenPreprocessed = false;
    }

    //for after preprocessing
    public Row(short heartRate, int stepCount, String sessionId)  {
        this.heartRate = heartRate;
        this.step_count = stepCount;
        this.sessionId = sessionId;
        hasBeenPreprocessed = true;
    }

    @NonNull
    @Override
    public String toString(){
        if (hasBeenPreprocessed) //the string contains minutes if it has not been processed
            return String.format("%d,%d,%d,%d\n", minutes, heartRate, step_count, label);
        else //the string contains a session_id if it has not been processed
            return String.format("%s,%d,%d,%d\n", sessionId, heartRate, step_count, label);

    }
}
