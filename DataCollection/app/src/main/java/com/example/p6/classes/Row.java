package com.example.p6.classes;

import androidx.annotation.NonNull;

import org.apache.commons.lang3.ObjectUtils;

public class Row{
    short heartRate;
    int step_count;
    byte label;
    short minutes;
    String session_id;
    boolean hasBeenPreprocessed;

    //for before preprocessing
    public Row(short heart_rate, int step_count, byte label, short minutes) {
        this.heartRate = heart_rate;
        this.step_count = step_count;
        this.label = label;
        this.minutes = minutes;
        hasBeenPreprocessed = false;
    }

    //for after preprocessing
    public Row(short heart_rate, int step_count, byte label, String session_id)  {
        this.heartRate = heart_rate;
        this.step_count = step_count;
        this.label = label;
        this.session_id = session_id;
        hasBeenPreprocessed = true;
    }

    @NonNull
    @Override
    public String toString(){
        if (hasBeenPreprocessed) //the string contains minutes if it has not been processed
            return String.format("%d,%d,%d,%d\n", minutes, heartRate, step_count, label);
        else //the string contains a session_id if it has not been processed
            return String.format("%s,%d,%d,%d\n", session_id, heartRate, step_count, label);

    }
}
